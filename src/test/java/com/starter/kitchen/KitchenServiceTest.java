package com.starter.kitchen;

import com.common.util.CodeUtil;
import com.starter.kitchen.mock.driver.MockDriverSystem;
import com.starter.kitchen.service.ActiveMqService;
import com.starter.kitchen.service.RedisService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.jms.Topic;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KitchenServiceTest {
    @Mock
    Scheduler scheduler;

    @Mock
    KitchenConfig kitchenConfig;

    @Mock
    KitchenSocket kitchenSocket;

    @Mock
    MockDriverSystem driverSystem;

    @Mock
    ActiveMqService activeMqService;

    @Mock
    Topic orderStatus;

    @Autowired
    RedisService redisService;

    KitchenService kitchenService;

    Order order;

    Shelf shelf;

    @BeforeEach
    public void initMock() throws SchedulerException {
        MockitoAnnotations.initMocks(this);

        // Init order and kitchen service
        order = new Order() {{
            setId(CodeUtil.getCode());
            setTemp("test");
        }};

        when(kitchenConfig.getShelves()).thenReturn(new String[]{order.getTemp()});
        when(kitchenConfig.getCapacities()).thenReturn(new int[]{1});
        when(kitchenConfig.getOverflow()).thenReturn(1);

        kitchenService = new KitchenService(
                kitchenConfig,
                kitchenSocket,
                redisService,
                activeMqService,
                orderStatus
        );

        // Mock shelf
        Map<String, Shelf> shelfMap = kitchenService.shelfMap;
        shelf = spy(shelfMap.get(order.getTemp()));
        shelfMap.put(order.getTemp(), shelf);

        // Mock dependencies
        kitchenService.scheduler = scheduler;
        kitchenService.driverSystem = driverSystem;
    }

    @AfterEach
    public void destroy() {
        shelf.getOrders().forEach(item -> {
            shelf.removeOrder(item);
        });
    }

    @Test
    public void testCook() {
        Order order = spy(this.order);

        // Shelf is not full
        when(shelf.isOverflowed()).thenReturn(false);

        // Mock functions
        KitchenService kitchenService = spy(this.kitchenService);
        doNothing().when(kitchenService).updateShelf();
        doNothing().when(kitchenService).notifyAdmin();
        doNothing().when(kitchenService).cancelJob();
        doNothing().when(kitchenService).scheduleJob();
        doNothing().when(kitchenService).waste(any());
        when(kitchenService.limitShelfSpace(any())).thenReturn(null);
        when(kitchenService.getOrderWithMinLife()).thenReturn(order);

        when(driverSystem.pickUpOrder(any())).thenReturn(1L);
        doNothing().when(activeMqService).sendMessage(any(), any());

        // Cook and pickup
        when(order.getPickupValue()).thenReturn(1.0);
        kitchenService.cook(order);

        verify(kitchenService, never()).waste(order);
        verify(kitchenService, times(1)).updateShelf();
        verify(kitchenService, times(1)).notifyAdmin();

        verify(kitchenService, times(1)).limitShelfSpace(any());
        verify(kitchenService, atLeastOnce()).getOrderWithMinLife();

        verify(kitchenService, times(1)).cancelJob();
        verify(kitchenService, times(1)).scheduleJob();

        verify(driverSystem, times(1)).pickUpOrder(order);
        verify(activeMqService, times(1)).sendMessage(orderStatus, order);

        // Waste directly
        when(order.getPickupValue()).thenReturn(-1.0);
        kitchenService.cook(order);

        verify(kitchenService, times(1)).waste(order);
        verify(kitchenService, times(2)).updateShelf();
        verify(kitchenService, times(2)).notifyAdmin();
    }

    @Test
    public void testWaste() {
        kitchenService.waste(order);

        verify(driverSystem, times(1)).cancelOrder(order);
        verify(activeMqService, times(1)).sendMessage(orderStatus, order);
    }

    @Test
    public void testPickedUp() {
        KitchenService kitchenService = spy(this.kitchenService);
        doNothing().when(kitchenService).updateShelf();
        doNothing().when(kitchenService).notifyAdmin();

        kitchenService.pickedUp(order);

        verify(kitchenService, times(1)).updateShelf();
        verify(kitchenService, times(1)).notifyAdmin();
    }

    @Test
    public void testUpdateShelf() {
        KitchenService kitchenService = spy(this.kitchenService);
        doNothing().when(kitchenService).waste(any());

        // Mock orders on shelf
        when(shelf.cleanOrders()).thenReturn(new ArrayList<Order>() {{
            add(order);
        }});

        // Verify
        kitchenService.updateShelf();
        verify(shelf, atLeastOnce()).cleanOrders();
        verify(kitchenService, atLeastOnce()).waste(any());
    }

    @Test
    public void testNotifyAdmin() {
        // Mock orders
        shelf.addOrder(order);
        shelf.addOrder(new Order() {{
            setId(CodeUtil.getCode());
        }});
        shelf.addOrder(new Order() {{
            setId(CodeUtil.getCode());
        }});

        when(shelf.getInfo()).thenCallRealMethod();

        // Verify
        kitchenService.notifyAdmin();
        verify(kitchenSocket, atLeastOnce()).sendMessage(anyString());
    }

    @Test
    public void testCancelJob() throws SchedulerException {
        // delete job
        kitchenService.shelfJob = new JobKey("test");
        when(scheduler.deleteJob(any())).thenReturn(true);

        kitchenService.cancelJob();
        verify(scheduler, times(1)).deleteJob(any());

        // catch exception
        kitchenService.shelfJob = new JobKey("test");
        doThrow(new SchedulerException("unit testing mock")).when(scheduler).deleteJob(any());

        kitchenService.cancelJob();
        verify(scheduler, times(2)).deleteJob(any());
    }

    @Test
    public void testScheduleJob() throws SchedulerException {
        KitchenService kitchenService = spy(this.kitchenService);

        // Mock scheduler
        when(scheduler.getContext()).thenReturn(new SchedulerContext());
        when(scheduler.scheduleJob(any(), any())).thenReturn(new Date());
        doNothing().when(scheduler).start();

        // No order
        when(kitchenService.getOrderWithMinLife()).thenReturn(null);
        kitchenService.scheduleJob();

        verify(scheduler, never()).scheduleJob(any(), any());
        verify(scheduler, never()).start();

        // Found order
        when(kitchenService.getOrderWithMinLife()).thenReturn(order);
        kitchenService.scheduleJob();

        verify(scheduler, times(1)).scheduleJob(any(), any());
        verify(scheduler, times(1)).start();

        // Exception
        doThrow(new SchedulerException("unit testing mock")).when(scheduler).start();
        kitchenService.scheduleJob();

        verify(scheduler, times(2)).scheduleJob(any(), any());
        verify(scheduler, times(2)).start();
    }

    @Test
    public void testGetOrderWithMinLive() {
        // Mock orders on shelf
        when(shelf.getOrderWithMinLife()).thenReturn(order);
        Assertions.assertEquals(order, kitchenService.getOrderWithMinLife());
        verify(shelf, times(1)).getOrderWithMinLife();

        // No proper one
        when(shelf.getOrderWithMinLife()).thenReturn(null);
        Assertions.assertNull(kitchenService.getOrderWithMinLife());
        verify(shelf, times(2)).getOrderWithMinLife();
    }

    @Test
    public void testGetOrderForSpace() {
        // Return order
        when(shelf.getOrderWithMinValue()).thenReturn(order);
        Assertions.assertEquals(order, kitchenService.getOrderForSpace(order.getTemp()));
        verify(shelf, atLeastOnce()).getOrderWithMinValue();

        // No proper one
        when(shelf.getOrderWithMinValue()).thenReturn(null);
        Assertions.assertNull(kitchenService.getOrderForSpace(order.getTemp()));
        verify(shelf, atLeast(2)).getOrderWithMinValue();
    }

    @Test
    public void testLimitShelfSpace() {
        KitchenService kitchenService = spy(this.kitchenService);
        when(kitchenService.limitShelfSpace(any())).thenCallRealMethod();

        // Shelf is not full
        when(shelf.isOverflowed()).thenReturn(false);

        Assertions.assertNull(kitchenService.limitShelfSpace(order.getTemp()));
        verify(shelf, times(1)).isOverflowed();

        // Shelf is full but overflow shelf has space
        when(shelf.isOverflowed()).thenReturn(true);
        when(shelf.getOverflowCount()).thenReturn(1);

        Assertions.assertNull(kitchenService.limitShelfSpace(order.getTemp()));
        verify(shelf, times(1)).getOverflowCount();

        // Need to remove one order to empty space
        when(shelf.getOverflowCount()).thenReturn(2);
        when(kitchenService.getOrderForSpace(order.getTemp())).thenReturn(order);
        doNothing().when(kitchenService).waste(any());
        doNothing().when(shelf).removeOrder(any());

        Assertions.assertEquals(order, kitchenService.limitShelfSpace(order.getTemp()));
        verify(kitchenService, times(1)).waste(any());
        verify(shelf, times(1)).removeOrder(any());
    }

    @Test
    public void testGetInfo() {
        // Mock orders
        shelf.addOrder(order);
        shelf.addOrder(new Order() {{
            setId(CodeUtil.getCode());
        }});

        when(shelf.getInfo()).thenCallRealMethod();

        // Get info
        List<Map<String, Object>> infoList = kitchenService.getInfo();
        verify(shelf, atLeastOnce()).getInfo();

        // Verify items and temp
        infoList.forEach(infoMap -> {
            List<Order> orders = (List<Order>) infoMap.get("items");
            String temp = (String) infoMap.get("temp");
            if (Shelf.OVERFLOW_FLAG.equals(temp)) {
                Assertions.assertTrue(orders.size() >= 1);
            } else {
                Assertions.assertEquals(1, orders.size());
                Assertions.assertTrue(order.getTemp().equals(temp));
            }
        });
    }
}
