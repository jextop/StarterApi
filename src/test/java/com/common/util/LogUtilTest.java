package com.common.util;

import com.starter.StarterApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = StarterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LogUtilTest {
    @Test
    public void testLog() {
        LogUtil.debug("debug", "message.");
        LogUtil.info("info", "message.");
        LogUtil.warn("warn", "message.");
        LogUtil.error("error", "message.");
    }

    @Test
    public void testUtil() {
        // To keep 100% unit-testing coverage
        Assertions.assertNotNull(new LogUtil());
    }
}
