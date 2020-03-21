package com.starter.kitchen.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ding
 */
public class RespUtil {
    public static Map<String, Object> ok() {
        return ok("OK");
    }

    public static Map<String, Object> ok(String msg) {
        return new HashMap<String, Object>() {{
            put("code", 200);
            put("msg", msg);
        }};
    }
}
