package com.common.util;

import com.common.util.ResUtil;
import com.starter.StarterApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * @author ding
 */
@SpringBootTest(classes = StarterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ResUtilTest {
    @Test
    public void testReadAsStr() throws IOException {
        Assertions.assertNull(ResUtil.readAsStr(null));
        Assertions.assertNull(ResUtil.readAsStr("un-existed.json"));
        Assertions.assertNotNull(ResUtil.readAsStr("kitchen_order.json"));
    }

    @Test
    public void testResUtil() {
        // To keep 100% unit-testing coverage
        Assertions.assertNotNull(new ResUtil());
    }
}
