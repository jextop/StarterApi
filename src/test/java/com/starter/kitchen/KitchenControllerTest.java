package com.starter.kitchen;

import com.starter.kitchen.mock.driver.MockDriverSystem;
import com.starter.kitchen.mock.order.MockOrderSystem;
import com.starter.mq.MqService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KitchenControllerTest {
    @Autowired
    KitchenController kitchenController;

    @Test
    public void testAuto() {
        // Mock
        MockOrderSystem orderSystem = spy(kitchenController.orderSystem);
        kitchenController.orderSystem = orderSystem;

        MockDriverSystem driverSystem = spy(kitchenController.driverSystem);
        kitchenController.driverSystem = driverSystem;

        doNothing().when(orderSystem).setAutoSendOrders(anyBoolean());
        doNothing().when(driverSystem).setAutoPickup(anyBoolean());

        when(orderSystem.isAutoSendOrders()).thenReturn(true);
        when(driverSystem.isAutoPickup()).thenReturn(true);

        // Switch auto mode
        Map<String, Object> ret = kitchenController.auto("{\n" +
                "    \"order\": \"0\",\n" +
                "    \"pickup\": \"0\"\n" +
                "  }");
        Assertions.assertTrue((boolean) ret.get("autoOrder"));
        Assertions.assertTrue((boolean) ret.get("autoPickup"));

        verify(orderSystem, times(1)).setAutoSendOrders(anyBoolean());
        verify(driverSystem, times(1)).setAutoPickup(anyBoolean());
    }

    @Test
    public void testCook() {
        // Mock
        MqService mqService = spy(kitchenController.mqService);
        kitchenController.mqService = mqService;

        doNothing().when(mqService).sendMessage(any(), any());

        // Cook
        Map<String, Object> ret = kitchenController.cook("{\n" +
                "    \"name\": \"Test\",\n" +
                "    \"temp\": \"frozen\",\n" +
                "    \"shelfLife\": 20,\n" +
                "    \"decayRate\": 0.63\n" +
                "  }");

        Assertions.assertNotNull(ret.get("id"));
        Assertions.assertEquals("Test", ret.get("name"));
        verify(mqService, times(1)).sendMessage(any(), any());
    }

    @Test
    public void testInfo() {
        // Mock
        KitchenService kitchenService = spy(kitchenController.kitchenService);
        kitchenController.kitchenService = kitchenService;

        when(kitchenService.getInfo()).thenReturn(new ArrayList<>());

        // Get info
        Map<String, Object> ret = kitchenController.info();

        Assertions.assertTrue(ret.containsKey("items"));
        verify(kitchenService, times(1)).getInfo();
    }
}
