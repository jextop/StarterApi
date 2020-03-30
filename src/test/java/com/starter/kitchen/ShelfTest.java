package com.starter.kitchen;

import com.common.util.CodeUtil;
import com.starter.kitchen.service.RedisService;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ShelfTest {
    Shelf shelf;

    @Autowired
    RedisService redisService;

    @BeforeEach
    public void init() {
        shelf = new Shelf(redisService, "test", 1);
    }

    @AfterEach
    public void destroy() {
        shelf.getOrders().forEach(item -> shelf.removeOrder(item));
    }

    @Test
    public void testGetTemp() {
        Assertions.assertEquals("test", shelf.getTemp());
    }

    @Test
    public void testOverflow() {
        // Add orders
        shelf.addOrder(new Order() {{
            setId(CodeUtil.getCode());
        }});
        Assertions.assertFalse(shelf.isOverflowed());
        Assertions.assertEquals(0, shelf.getOverflowCount());

        shelf.addOrder(new Order() {{
            setId(CodeUtil.getCode());
        }});
        Assertions.assertTrue(shelf.isOverflowed());
        Assertions.assertEquals(1, shelf.getOverflowCount());
    }

    @Test
    public void testAddOrder() {
        // Empty shelf
        Assertions.assertTrue(CollectionUtils.isEmpty(shelf.getOrders()));

        // Add order
        shelf.addOrder(new Order() {{
            setId(CodeUtil.getCode());
        }});
        Assertions.assertEquals(1, shelf.getOrders().size());
    }

    @Test
    public void testRemoveOrder() {
        Order item = new Order() {{
            setId(CodeUtil.getCode());
        }};

        // Shelf with order
        shelf.addOrder(item);
        Assertions.assertEquals(1, shelf.getOrders().size());

        // Remove order
        shelf.removeOrder(item);
        Assertions.assertTrue(CollectionUtils.isEmpty(shelf.getOrders()));
    }

    @Test
    public void testCleanOrders() {
        // Add orders
        shelf.addOrder(new Order() {{
            setId(CodeUtil.getCode());
            setShelfLife(20);
            setCookTime(System.currentTimeMillis() - 1000 * 3);
        }});
        shelf.addOrder(new Order() {{
            setId(CodeUtil.getCode());
            setShelfLife(10);
            setCookTime(System.currentTimeMillis() - 1000 * 3);
        }});
        shelf.addOrder(new Order() {{
            setId(CodeUtil.getCode());
            setShelfLife(2);
            setCookTime(System.currentTimeMillis() - 1000 * 3);
        }});

        Assertions.assertEquals(1, shelf.cleanOrders().size());
        Assertions.assertEquals(2, shelf.getOrders().size());
    }

    @Test
    public void testGetOrderWithMinIntervalToZeroValue() {
        // Add orders
        Order order = new Order() {{
            setId(CodeUtil.getCode());
            setShelfLife(12);
            setDecayRate(0.9);
            setCookTime(System.currentTimeMillis() - 1000 * 3);
        }};
        shelf.addOrder(order);

        shelf.addOrder(new Order() {{
            setId(CodeUtil.getCode());
            setShelfLife(10);
            setDecayRate(0.1);
            setCookTime(System.currentTimeMillis() - 1000 * 3);
        }});

        Assertions.assertEquals(order, shelf.getOrderWithMinLife());
    }

    @Test
    public void testGetOrderWithMinValue() {
        // Add orders
        Order order = new Order() {{
            setId(CodeUtil.getCode());
            setShelfLife(10);
            setDecayRate(0.1);
            setCookTime(System.currentTimeMillis() - 1000 * 3);
        }};
        shelf.addOrder(order);

        shelf.addOrder(new Order() {{
            setId(CodeUtil.getCode());
            setShelfLife(10);
            setCookTime(System.currentTimeMillis() - 1000 * 3);
        }});

        Assertions.assertEquals(order, shelf.getOrderWithMinValue());
    }

    @Test
    public void testGetOrders() {
        // Shelf with order
        shelf.addOrder(new Order() {{
            setId(CodeUtil.getCode());
        }});

        // Get orders
        Assertions.assertEquals(1, shelf.getOrders().size());
    }

    @Test
    public void testGetInfo() {
        // Add orders
        shelf.addOrder(new Order() {{
            setId(CodeUtil.getCode());
        }});
        shelf.addOrder(new Order() {{
            setId(CodeUtil.getCode());
        }});

        // Get orders
        Map<String, Object> info = shelf.getInfo();
        List<Order> orders = (List<Order>) info.get("items");
        Assertions.assertEquals(1, orders.size());

        // Get overflowed ones
        List<Order> overflowOrders = (List<Order>) info.get(Shelf.OVERFLOW_FLAG);
        Assertions.assertEquals(1, overflowOrders.size());
    }
}
