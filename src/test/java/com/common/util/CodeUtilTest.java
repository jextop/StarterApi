package com.common.util;

import com.starter.StarterApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashSet;
import java.util.Set;

/**
 * @author ding
 */
@SpringBootTest(classes = StarterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CodeUtilTest {
    @Test
    public void testGetCode() {
        final int count = Short.MAX_VALUE * 10;
        Set<String> set = new HashSet<String>();
        for (int i = 0; i < count; i++) {
            String code = CodeUtil.getCode();
            if (set.contains(code)) {
                continue;
            }
            set.add(code);
        }
        Assertions.assertEquals(count, set.size());
    }

    @Test
    public void testUtil() {
        // To keep 100% unit-testing coverage
        Assertions.assertNotNull(new CodeUtil());
    }
}
