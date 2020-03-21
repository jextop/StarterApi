package com.starter.kitchen.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RespUtilTest {
    @Test
    public void testOk() {
        Assertions.assertEquals(RespUtil.ok(), RespUtil.ok("OK"));
    }

    @Test
    public void testUtil() {
        // To keep 100% unit-testing coverage
        Assertions.assertNotNull(new RespUtil());
    }
}
