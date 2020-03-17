package com.common.util;

import com.starter.StarterApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = StarterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class LogUtilTest {
    @Test
    public void testLog() {
        LogUtil.debug("debug", "message.");
        LogUtil.info("info", "message.");
        LogUtil.warn("warn", "message.");
        LogUtil.error("error", "message.");
    }
}
