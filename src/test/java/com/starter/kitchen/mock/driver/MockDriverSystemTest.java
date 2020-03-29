package com.starter.kitchen.mock.driver;

import com.common.util.CodeUtil;
import com.starter.kitchen.KitchenService;
import com.starter.kitchen.Order;
import com.starter.kitchen.service.ActiveMqService;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.mockito.ArgumentMatchers.any;
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
public class MockDriverSystemTest {
    @Autowired
    MockDriverSystem driverSystem;

    @Autowired
    MockDriverConfig driverConfig;

    @Mock
    Scheduler scheduler;

    @Mock
    ActiveMqService activeMqService;

    @Mock
    KitchenService kitchenService;

    @BeforeEach
    public void initMock() throws SchedulerException {
        MockitoAnnotations.initMocks(this);

        // Mock
        driverSystem.scheduler = scheduler;
        driverSystem.activeMqService = activeMqService;
        driverSystem.kitchenService = kitchenService;
    }

    @Test
    public void testPickupOrder() throws SchedulerException {
        // Mock
        MockDriverSystem driverSystem = spy(this.driverSystem);
        doNothing().when(driverSystem).cancelJob();
        doNothing().when(driverSystem).scheduleJob();
        when(driverSystem.getMinPickupTime()).thenReturn(System.currentTimeMillis() * 2);

        // Turn on auto mode
        driverSystem.setAutoPickup(true);

        // Pickup order
        Order order = new Order() {{
            setId(CodeUtil.getCode());
            setTemp("hot");
        }};

        int count = 5;
        for (int i = 0; i < count; i++) {
            long interval = System.currentTimeMillis();
            long pickupTime = driverSystem.pickUpOrder(order);
            interval = (pickupTime - interval) / 1000;
            Assertions.assertTrue(
                    interval >= driverConfig.getMin()
                            && interval <= driverConfig.getMax()
            );
        }

        // verify
        Assertions.assertTrue(driverSystem.orderMap.containsKey(order.getId()));
        verify(driverSystem, times(count)).getMinPickupTime();
        verify(driverSystem, times(count)).cancelJob();
        verify(driverSystem, times(count)).scheduleJob();

        // Turn off
        driverSystem.setAutoPickup(false);
        Assertions.assertEquals(0, driverSystem.pickUpOrder(order));
    }

    @Test
    public void testScheduleJob() throws SchedulerException {
        MockDriverSystem driverSystem = spy(this.driverSystem);

        // Mock scheduler
        when(scheduler.getContext()).thenReturn(new SchedulerContext());
        when(scheduler.scheduleJob(any(), any())).thenReturn(new Date());
        doNothing().when(scheduler).start();

        // No order
        when(driverSystem.getMinPickupTime()).thenReturn(0L);
        driverSystem.scheduleJob();

        verify(scheduler, never()).scheduleJob(any(), any());
        verify(scheduler, never()).start();

        // Found order
        when(driverSystem.getMinPickupTime()).thenReturn(System.currentTimeMillis());
        driverSystem.scheduleJob();

        verify(scheduler, times(1)).scheduleJob(any(), any());
        verify(scheduler, times(1)).start();

        // Exception
        doThrow(new SchedulerException("unit testing mock")).when(scheduler).start();
        driverSystem.scheduleJob();

        verify(scheduler, times(2)).scheduleJob(any(), any());
        verify(scheduler, times(2)).start();
    }

    @Test
    public void testCancelOrder() throws SchedulerException {
        // Un-existed order
        Order order = new Order() {{
            setId(CodeUtil.getCode());
            setTemp("frozen");
        }};

        driverSystem.cancelOrder(order);
        Assertions.assertFalse(driverSystem.orderMap.containsKey(order.getId()));

        // Put order
        Map<String, Order> orderJobMap = driverSystem.orderMap;
        orderJobMap.put(order.getId(), order);

        // Cancel order
        driverSystem.cancelOrder(order);
        Assertions.assertFalse(driverSystem.orderMap.containsKey(order.getId()));
    }

    @Test
    public void testCancelJob() throws SchedulerException {
        // delete job
        driverSystem.pickupJob = new JobKey("test");
        when(scheduler.deleteJob(any())).thenReturn(true);

        driverSystem.cancelJob();
        verify(scheduler, times(1)).deleteJob(any());

        // catch exception
        driverSystem.pickupJob = new JobKey("test");
        doThrow(new SchedulerException("unit testing mock")).when(scheduler).deleteJob(any());

        driverSystem.cancelJob();
        verify(scheduler, times(2)).deleteJob(any());
    }

    @Test
    public void testGetMinPickupTime() {
        // No order
        driverSystem.orderMap.clear();
        Assertions.assertEquals(0, driverSystem.getMinPickupTime());

        // Sorted orders
        Order order = new Order() {{
            setId(CodeUtil.getCode());
            setPickupTime(33);
        }};

        driverSystem.orderMap = new ConcurrentHashMap<String, Order>() {{
            put(order.getId(), order);
            put("test", new Order() {{
                setId("test");
                setPickupTime(44);
            }});
        }};
        Assertions.assertEquals(33, driverSystem.getMinPickupTime());
    }

    @Test
    public void testDelivery() throws InterruptedException {
        // Mock
        doNothing().when(activeMqService).sendMessage(any(), any());
        doNothing().when(kitchenService).pickedUp(any());

        // Delivery
        Order order = new Order() {{
            setId(CodeUtil.getCode());
            setPickupTime(System.currentTimeMillis() - 3);
        }};

        driverSystem.orderMap = new ConcurrentHashMap<String, Order>() {{
            put(order.getId(), order);
            put("test", new Order() {{
                setId("test");
                setPickupTime(System.currentTimeMillis() + 100);
            }});
        }};
        driverSystem.delivery();

        // Verify
        Assertions.assertFalse(driverSystem.orderMap.containsKey(order.getId()));
        verify(activeMqService, times(1)).sendMessage(any(), any());
        verify(kitchenService, times(1)).pickedUp(any());
    }

    @Test
    public void testAutoPickup() {
        // Mock
        when(kitchenService.getInfo()).thenReturn(new ArrayList<Map<String, Object>>() {{
            add(new HashMap<String, Object>() {{
                put("items", new ArrayList<Order>() {{
                    add(new Order() {{
                        setId(CodeUtil.getCode());
                    }});
                }});
            }});
        }});

        MockDriverSystem driverSystem = spy(this.driverSystem);
        when(driverSystem.pickUpOrder(any())).thenReturn(0L);

        // Turn on auto mode
        driverSystem.setAutoPickup(true);
        Assertions.assertTrue(driverSystem.isAutoPickup());
        verify(driverSystem, times(1)).pickUpOrder(any());
    }
}
