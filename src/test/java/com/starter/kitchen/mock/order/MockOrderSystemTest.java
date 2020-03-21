package com.starter.kitchen.mock.order;

import com.starter.kitchen.service.ActiveMqService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockOrderSystemTest {
    @Autowired
    MockOrderSystem orderSystem;

    @Test
    public void testSendOrders() {
        // Mock
        ActiveMqService activeMqService = mock(ActiveMqService.class);
        orderSystem.activeMqService = activeMqService;
        doNothing().when(activeMqService).sendMessage(any(), any());

        // Turn on auto mode
        orderSystem.setAutoSendOrders(true);

        // Send orders
        int count = orderSystem.sendOrders();
        Assertions.assertTrue(count > 0);
        verify(activeMqService, times(count)).sendMessage(any(), any());

        // Empty orderList
        orderSystem.orderList = null;
        Assertions.assertEquals(0, orderSystem.sendOrders());

        // Turn off
        orderSystem.setAutoSendOrders(false);
        Assertions.assertEquals(0, orderSystem.sendOrders());
    }

    @Test
    public void testAutoSendOrders() {
        orderSystem.setAutoSendOrders(false);
        Assertions.assertFalse(orderSystem.isAutoSendOrders());
    }

    @Test
    public void testOrderJob() {
        Assertions.assertNotNull(orderSystem.orderJob());
    }

    @Test
    public void testOrderTrigger() {
        Assertions.assertNotNull(orderSystem.orderTrigger());
    }
}
