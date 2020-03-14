package com.common.util;

import com.starter.StarterApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = StarterApplication.class)
public class MacUtilTest {
    @Test
    public void testGetMacAddr() {
        String ret = MacUtil.gtMacAddr();
        System.out.println(ret);
        Assertions.assertNotNull(ret);
    }
}
