package com.starter.kitchen.mock.driver;

import com.starter.kitchen.KitchenService;
import com.starter.kitchen.Order;
import com.starter.mq.MqService;
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
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.jms.Topic;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ding
 */
@Service
@Lazy
public class MockDriverSystem {
    Scheduler scheduler;
    JobKey pickupJob;
    Map<String, Order> orderMap;

    private volatile boolean autoPickup;
    private MockDriverConfig driverConfig;

    MqService mqService;
    private Topic orderStatus;

    @Autowired
    KitchenService kitchenService;

    @Autowired
    public MockDriverSystem(
            MockDriverConfig driverConfig,
            MqService mqService,
            Topic orderStatus
    ) throws SchedulerException {
        this.driverConfig = driverConfig;
        this.autoPickup = driverConfig.isAuto();
        this.mqService = mqService;
        this.orderStatus = orderStatus;

        orderMap = new ConcurrentHashMap<>();

        // Scheduler
        scheduler = StdSchedulerFactory.getDefaultScheduler();
    }

    public long pickUpOrder(Order order) {
        if (!autoPickup || order == null) {
            return 0;
        }

        // Random pickup time
        long pickupTime = System.currentTimeMillis() + driverConfig.getInterval() * 1000;
        order.setPickupTime(pickupTime);

        // Remember the job info.
        orderMap.put(order.getId(), order);

        // Schedule pickup job if the new one is minimum
        if (pickupTime <= getMinPickupTime()) {
            cancelJob();
            scheduleJob();
        }

        return pickupTime;
    }

    public void cancelOrder(Order order) {
        orderMap.remove(order.getId());
    }

    protected long getMinPickupTime() {
        if (MapUtils.isEmpty(orderMap)) {
            return 0;
        }

        // Find the minimum pickup time
        List<Order> orders = new ArrayList<>(orderMap.values());

        // Sort by pickup time
        orders.sort(Comparator.comparing(Order::getPickupTime));
        return orders.get(0).getPickupTime();
    }

    protected void scheduleJob() {
        long time = getMinPickupTime();
        if (time == 0) {
            return;
        }

        // Create job to pickup
        time = Math.max(getMinPickupTime(), System.currentTimeMillis() + 100);

        JobDetail job = JobBuilder.newJob(MockDriverJob.class)
                .storeDurably()
                .build();

        pickupJob = job.getKey();

        SimpleTrigger trigger = TriggerBuilder.newTrigger()
                .forJob(job)
                .startAt(new Date(time))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();

        // Schedule job
        try {
            scheduler.getContext().put("driver", this);

            scheduler.scheduleJob(job, trigger);
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    protected void cancelJob() {
        if (pickupJob != null) {
            synchronized (KitchenService.class) {
                if (pickupJob != null) {
                    // Remove the pickup job
                    try {
                        scheduler.deleteJob(pickupJob);
                    } catch (SchedulerException e) {
                        e.printStackTrace();
                    } finally {
                        pickupJob = null;
                    }
                }
            }
        }
    }

    protected void delivery() {
        // Sort by pickup time
        List<Order> orders = new ArrayList<>(orderMap.values());
        orders.sort(Comparator.comparing(Order::getPickupTime));

        for (Order order : orders) {
            if (order.getPickupTime() <= System.currentTimeMillis()) {
                // Remove the order
                orderMap.remove(order.getId());

                // Do pick up from kitchen
                order.setStatus("picked_up");
                kitchenService.pickedUp(order);

                // Send status to order system
                mqService.sendMessage(orderStatus, order);
            } else {
                // Orders are sorted by pickup time
                break;
            }
        }
    }

    public boolean isAutoPickup() {
        return autoPickup;
    }

    public void setAutoPickup(boolean autoPickup) {
        this.autoPickup = autoPickup;

        // Pickup the existed orders when turn on auto mode
        List<Map<String, Object>> infoList = kitchenService.getInfo();
        if (autoPickup && CollectionUtils.isNotEmpty(infoList)) {
            infoList.forEach(infoMap -> {
                List<Order> orderList = (List<Order>) infoMap.get("items");

                orderList.forEach(order -> {
                    if (!orderMap.containsKey(order.getId())) {
                        pickUpOrder(order);
                    }
                });
            });
        }
    }
}
