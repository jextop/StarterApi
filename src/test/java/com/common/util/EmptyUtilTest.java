package com.common.util;

import com.starter.StarterApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by user on 2017/9/23.
 */
@SpringBootTest(classes = StarterApplication.class)
public class EmptyUtilTest {
    @Test
    public void testIsEmptyStr() {
        Map<String, Boolean> mapIO = new HashMap<String, Boolean>() {{
            put(null, true);
            put("", true);
            put(" ", true);
            put("t", false);
        }};

        for (Map.Entry<String, Boolean> io : mapIO.entrySet()) {
            boolean ret = StrUtil.isEmpty(io.getKey());
            Assertions.assertEquals(io.getValue(), ret);
        }
    }

    @Test
    public void testIsEmptyArr() {
        Map<String[], Boolean> mapIO = new HashMap<String[], Boolean>() {{
            put(null, true);
            put(new String[]{}, true);
            put(new String[]{"test"}, false);
        }};

        for (Map.Entry<String[], Boolean> io : mapIO.entrySet()) {
            boolean ret = EmptyUtil.isEmpty(io.getKey());
            Assertions.assertEquals(io.getValue(), ret);
        }
    }
}
