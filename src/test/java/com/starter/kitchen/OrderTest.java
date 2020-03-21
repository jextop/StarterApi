package com.starter.kitchen;

import com.alibaba.fastjson.JSON;
import com.common.util.CodeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrderTest {
    @Test
    public void testOrder() {
        Order item = JSON.parseObject("{\n" +
                "    \"name\": \"Banana Split\",\n" +
                "    \"temp\": \"frozen\",\n" +
                "    \"shelfLife\": 20,\n" +
                "    \"decayRate\": 0.63,\n" +
                "    \"id\": \"id\",\n" +
                "    \"status\": \"ready\",\n" +
                "    \"cookTime\": 20,\n" +
                "    \"pickupTime\": 20,\n" +
                "  }", Order.class
        );

        Assertions.assertNotNull(item);
        Assertions.assertTrue(StringUtils.isNotBlank(item.toString()));
        Assertions.assertNotNull(JSON.toJSONString(item));
    }

    @Test
    public void testGetValue() {
        Order item = new Order() {{
            setShelfLife(300);
            setDecayRate(0.5);
            setCookTime(System.currentTimeMillis() - 1000 * 100);
            setPickupTime(System.currentTimeMillis() + 1000 * 50);
        }};

        Assertions.assertEquals(150, item.getValue());
        Assertions.assertEquals(0.5, item.getNormalizedValue());
        Assertions.assertEquals(75, item.getPickupValue());
    }

    @Test
    public void testGetLife() {
        Order item = new Order() {{
            setShelfLife(300);
            setDecayRate(0.5);
            setCookTime(System.currentTimeMillis() - 1000 * 100);
        }};

        Assertions.assertEquals(100, item.getLife());
    }

    @Test
    public void testCompareTo() {
        Order item1 = new Order() {{
            setShelfLife(250);
            setDecayRate(0.8);
            setCookTime(System.currentTimeMillis() - 1000 * 100);
        }};

        Order item2 = new Order() {{
            setShelfLife(200);
            setDecayRate(0.2);
            setCookTime(System.currentTimeMillis() - 1000 * 100);
        }};

        Assertions.assertTrue(item1.compareTo(item2) < 0);
    }

    @Test
    public void testEquals() {
        Order item1 = new Order() {{
            setId(CodeUtil.getCode());
        }};

        Order item2 = new Order() {{
            setId(CodeUtil.getCode());
        }};

        Assertions.assertNotEquals(item1, item2);

        Order item3 = new Order() {{
            setId(item2.getId());
        }};
        Assertions.assertEquals(item3, item2);
    }
}
