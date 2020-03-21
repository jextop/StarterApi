package com.starter.kitchen.mock;

import com.starter.kitchen.Shelf;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

/**
 * @author ding
 */
public class MockDashboard {
    public static synchronized void print(List<Map<String, Object>> infoList) {
        System.out.println(StringUtils.rightPad("", 130, "="));

        // Combine the shelf info
        int i = 0;
        do {
            StringBuilder sb = new StringBuilder();

            int j = i;
            infoList.forEach(infoMap -> {
                List<Map<String, Object>> orderList = (List<Map<String, Object>>) infoMap.get("items");
                if (j == 0) {
                    sb.append(String.format("%26s(%2d) ", infoMap.get("temp"), orderList.size()));
                } else {
                    if (j <= orderList.size()) {
                        Map<String, Object> order = orderList.get(j - 1);
                        sb.append(String.format("%24s: %.2f %s",
                                order.get("name"),
                                order.get("normalizedValue"),
                                Shelf.OVERFLOW_FLAG.equals(infoMap.get("temp")) ? order.get("temp") : ""
                        ));
                    } else {
                        sb.append(String.format("%24s  %4s ", "", ""));
                    }
                }
            });

            if (StringUtils.isBlank(sb.toString())) {
                break;
            }

            // Print info in console
            System.out.println(sb.toString());

            // Next line
            i++;
        } while (i < 21);
    }
}
