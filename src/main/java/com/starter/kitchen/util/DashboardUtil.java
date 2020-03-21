package com.starter.kitchen.util;

import com.starter.kitchen.Order;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ding
 */
public class DashboardUtil {
    public static List<Map<String, Object>> simplify(List<Map<String, Object>> infoList) {
        // Reduce the data package size by keeping only the needed fields
        infoList.forEach(infoMap -> {
            List<Order> orderList = (List<Order>) infoMap.get("items");

            // Sort by normalized value
            orderList.sort(Comparator.comparing(Order::getNormalizedValue));

            List<Map<String, Object>> itemList = new ArrayList<Map<String, Object>>();
            orderList.forEach(order -> {
                itemList.add(new HashMap<String, Object>() {{
                    put("temp", order.getTemp());
                    put("name", order.getName());
                    put("normalizedValue", Double.valueOf(String.format("%.2f", order.getNormalizedValue())));
                }});
            });
            infoMap.put("items", itemList);
        });

        return infoList;
    }
}
