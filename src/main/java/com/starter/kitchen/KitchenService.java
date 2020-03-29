package com.starter.kitchen;

import com.alibaba.fastjson.JSON;
import com.starter.kitchen.mock.MockDashboard;
import com.starter.kitchen.mock.driver.MockDriverSystem;
import com.starter.kitchen.service.ActiveMqService;
import com.starter.kitchen.service.RedisService;
import com.starter.kitchen.util.DashboardUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.jms.Topic;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Kitchen cooks, puts to shelf and calls driver to pickup.
 * Kitchen manages the virtual overflow shelf.
 *
 * @author ding
 */
@Service
@Configuration
public class KitchenService {
    Scheduler scheduler;
    JobKey shelfJob;
    Map<String, Shelf> shelfMap;

    private KitchenConfig kitchenConfig;
    private KitchenSocket kitchenSocket;

    private ActiveMqService activeMqService;
    private Topic orderStatus;

    @Autowired
    MockDriverSystem driverSystem;

    @Autowired
    public KitchenService(
            KitchenConfig kitchenConfig,
            KitchenSocket kitchenSocket,
            RedisService redisService,
            ActiveMqService activeMqService,
            Topic orderStatus
    ) throws SchedulerException {
        this.kitchenConfig = kitchenConfig;
        this.kitchenSocket = kitchenSocket;

        this.activeMqService = activeMqService;
        this.orderStatus = orderStatus;

        // Initiate the shelves
        int count = kitchenConfig.getShelves().length;
        this.shelfMap = new HashMap<>(count);

        for (int i = 0; i < count; i++) {
            String temp = kitchenConfig.getShelves()[i];
            Shelf shelf = new Shelf(redisService, temp, kitchenConfig.getCapacities()[i]);
            this.shelfMap.put(temp, shelf);
        }

        // Scheduler
        scheduler = StdSchedulerFactory.getDefaultScheduler();
    }

    public void cook(Order order) {
        // Cook instant
        order.setCookTime(System.currentTimeMillis());
        order.setStatus("ready");

        // Call driver
        driverSystem.pickUpOrder(order);
        if (order.getPickupValue() < 0) {
            // Driver can't come in time, waste directly.
            waste(order);

            // Update shelves
            updateShelf();

            // Update admin page
            notifyAdmin();
            return;
        }

        // Put order on shelf
        shelfMap.get(order.getTemp()).addOrder(order);

        // Update shelves
        updateShelf();

        // Remove one order if no proper shelf space.
        limitShelfSpace(order.getTemp());

        // Re-schedule shelf job if the new one has minimum zero-value time
        Order minOrder = getOrderWithMinLife();
        if (order.equals(minOrder)) {
            cancelJob();
            scheduleJob();
        }

        // Update admin page
        notifyAdmin();

        // Send status to order system
        activeMqService.sendMessage(orderStatus, order);
    }

    public void waste(Order order) {
        order.setStatus("waste");
        driverSystem.cancelOrder(order);

        // Send status to order system
        activeMqService.sendMessage(orderStatus, order);
    }

    public void pickedUp(Order order) {
        // Remove order from shelf
        shelfMap.get(order.getTemp()).removeOrder(order);

        // Update shelves
        updateShelf();

        // Update admin page
        notifyAdmin();
    }

    protected void updateShelf() {
        // Clean orders with zero values
        for (Shelf shelf : shelfMap.values()) {
            List<Order> orders = shelf.cleanOrders();

            // Send status to order system
            orders.forEach(this::waste);
        }
    }

    protected void notifyAdmin() {
        // Send message to admin page
        List<Map<String, Object>> infoList = getInfo();
        DashboardUtil.simplify(infoList);

        // Send web socket message
        kitchenSocket.sendMessage(JSON.toJSONString(infoList));

        // Print text message
        MockDashboard.print(infoList);
    }

    protected void cancelJob() {
        if (shelfJob != null) {
            synchronized (KitchenService.class) {
                if (shelfJob != null) {
                    // Remove the shelf job
                    try {
                        scheduler.deleteJob(shelfJob);
                    } catch (SchedulerException e) {
                        e.printStackTrace();
                    } finally {
                        shelfJob = null;
                    }
                }
            }
        }
    }

    protected void scheduleJob() {
        // Find the minimum interval to zero value
        Order minOrder = getOrderWithMinLife();
        if (minOrder == null) {
            return;
        }

        // Create job to update shelf
        double interval = minOrder.getLife();
        long time = System.currentTimeMillis() + Math.max(100, (long) (interval * 1000));

        JobDetail job = JobBuilder.newJob(KitchenJob.class).build();
        shelfJob = job.getKey();

        SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .forJob(job)
                .startAt(new Date(time))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();

        // Schedule job
        try {
            scheduler.getContext().put(KitchenService.class.getSimpleName(), this);

            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    protected Order getOrderWithMinLife() {
        Order minOrder = null;
        for (Shelf shelf : shelfMap.values()) {
            // Find the order with minimum time to be zero-value waste
            Order order = shelf.getOrderWithMinLife();
            if (order == null) {
                continue;
            }

            if (minOrder == null || Double.compare(minOrder.getLife(), order.getLife()) > 0) {
                minOrder = order;
            }
        }
        return minOrder;
    }

    protected Order getOrderForSpace(String temp) {
        // Find the order with minimum value on the overflow (any types) or same temp shelf
        Order minOrder = null;
        for (Shelf shelf : shelfMap.values()) {
            if (shelf.getTemp().equals(temp) || shelf.isOverflowed()) {
                Order order = shelf.getOrderWithMinValue();
                if (order == null) {
                    continue;
                }

                if (minOrder == null || minOrder.compareTo(order) > 0) {
                    minOrder = order;
                }
            }
        }
        return minOrder;
    }

    protected Order limitShelfSpace(String temp) {
        // Check if space is overflowed
        if (!shelfMap.containsKey(temp) || !shelfMap.get(temp).isOverflowed()) {
            return null;
        }

        // Check if the overflow shelf is full
        int count = 0;
        for (Shelf shelf : shelfMap.values()) {
            count += shelf.getOverflowCount();
        }

        if (count <= kitchenConfig.getOverflow()) {
            return null;
        }

        // Remove order to empty space
        Order minOrder = getOrderForSpace(temp);
        if (minOrder != null) {
            shelfMap.get(minOrder.getTemp()).removeOrder(minOrder);

            waste(minOrder);
        }
        return minOrder;
    }

    public List<Map<String, Object>> getInfo() {
        // Merge overflow orders into a virtual shelf
        List<Order> overflowOrders = new ArrayList<>();

        // Get shelf info
        List<Map<String, Object>> shelves = new ArrayList<>();
        shelfMap.values().forEach(shelf -> {
            Map<String, Object> info = shelf.getInfo();
            if (MapUtils.isNotEmpty(info)) {
                // Check overflow orders
                List<Order> orders = (List<Order>) info.get(Shelf.OVERFLOW_FLAG);
                if (CollectionUtils.isNotEmpty(orders)) {
                    overflowOrders.addAll(orders);
                }

                info.remove(Shelf.OVERFLOW_FLAG);
                shelves.add(info);
            }
        });

        // Sort by normalized value
        overflowOrders.sort(Comparator.comparing(Order::getNormalizedValue));

        // Set overflow orders
        shelves.add(new HashMap<String, Object>(2) {{
            put("temp", Shelf.OVERFLOW_FLAG);
            put("items", overflowOrders);
        }});
        return shelves;
    }
}
