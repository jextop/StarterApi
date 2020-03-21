package com.starter.exception;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccessLimitExceptionTest {
    @Test
    public void test() {
        Assertions.assertNotNull(new AccessLimitException());
    }
}
