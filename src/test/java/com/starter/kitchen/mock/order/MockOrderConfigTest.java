package com.starter.kitchen.mock.order;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockOrderConfigTest {
    @Autowired
    MockOrderConfig orderConfig;

    @Test
    public void testConfig() {
        // To keep 100% unit-testing coverage
        Assertions.assertNotNull(orderConfig.toString());

        Assertions.assertNotNull(orderConfig.getLambda());
    }
}
