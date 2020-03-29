package com.starter.kitchen.mock;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockDashboardTest {
    @Test
    public void testPrint() {
        // Mock
        Map map = mock(Map.class);
        when(map.get("name")).thenReturn("mock");
        when(map.get("normalizedValue")).thenReturn(0.5);

        List<Map<String, Object>> infoList = new ArrayList<Map<String, Object>>() {{
            add(new HashMap<String, Object>() {{
                put("items", new ArrayList<Object>() {{
                    add(new HashMap<String, Object>() {{
                        put("name", "test");
                        put("normalizedValue", 0.333);
                    }});
                    add(map);
                }});
            }});
        }};

        // Verify
        MockDashboard.print(infoList);
        verify(map, times(2)).get(any());
    }

    @Test
    public void testUtil() {
        // To keep 100% unit-testing coverage
        Assertions.assertNotNull(new MockDashboard());
    }
}
