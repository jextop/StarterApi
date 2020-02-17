package com.common.util;

public class CodeUtil {
    private static int MAX_LEN = 16;

    /**
     * Generate the unique 24-number code: yyyyMMdd + time
     */
    public static String getCode() {
        String timeStr = String.format("%016d", System.nanoTime());
        int len = timeStr.length();
        if (len > MAX_LEN) {
            timeStr = timeStr.substring(len - MAX_LEN, len);
        }
        return String.format("%s%s", DateUtil.getTodayStr("yyyyMMdd"), timeStr);
    }
}
