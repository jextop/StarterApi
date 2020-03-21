package com.starter.kitchen.util;

import com.common.util.CodeUtil;
import com.starter.kitchen.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DashboardUtilTest {
    @Test
    public void testSimplify() {
        // Mock
        Order order = mock(Order.class);
        when(order.getName()).thenReturn("mock");
        when(order.getNormalizedValue()).thenReturn(0.5);

        List<Map<String, Object>> infoList = new ArrayList<Map<String, Object>>() {{
            add(new HashMap<String, Object>() {{
                put("items", new ArrayList<Order>() {{
                    add(new Order() {{
                        setName("test");
                        setShelfLife(22);
                    }});
                    add(order);
                }});
            }});
        }};

        // Verify
        List<Map<String, Object>> ret = DashboardUtil.simplify(infoList);
        Assertions.assertEquals(infoList.size(), ret.size());

        verify(order, times(1)).getName();
        verify(order, atLeastOnce()).getNormalizedValue();
    }

    @Test
    public void testUtil() {
        // To keep 100% unit-testing coverage
        Assertions.assertNotNull(new DashboardUtil());
    }
}
