package com.starter.annotation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccessLimitedTest {
    @AccessLimited
    @Test
    public void testAnnotation() throws NoSuchMethodException {
        Method method = AccessLimitedTest.class.getMethod("testAnnotation");
        AccessLimited annotation = method.getAnnotation(AccessLimited.class);
        Assertions.assertNotNull(annotation.toString());
    }
}
