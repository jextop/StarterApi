package com.common.util;

import java.util.Random;

public class CodeUtil {
    private static Random random;
    private static int TIME_LEN = 16;

    static {
        random = new Random();
    }

    /**
     * Generate the unique 24-number code: yyMMdd + nano_time(15) + 000
     */
    public static String getCode() {
        String timeStr = String.format("%015d", System.nanoTime());
        int len = timeStr.length();
        if (len > TIME_LEN) {
            timeStr = timeStr.substring(len - TIME_LEN, len);
        }
        return String.format("%s%s%d", DateUtil.getTodayStr("yyMMdd"), timeStr, random.nextInt(1000));
    }
}
