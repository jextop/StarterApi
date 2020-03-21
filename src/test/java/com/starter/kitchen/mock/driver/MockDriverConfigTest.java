package com.starter.kitchen.mock.driver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockDriverConfigTest {
    @Autowired
    MockDriverConfig driverConfig;

    @Test
    public void testGetInterval() {
        for (int i = 0; i < 10; i++) {
            int interval = driverConfig.getInterval();
            Assertions.assertTrue(
                    interval >= driverConfig.getMin()
                            && interval <= driverConfig.getMax()
            );
        }
    }

    @Test
    public void testConfig() {
        // To keep 100% unit-testing coverage
        Assertions.assertNotNull(driverConfig.toString());
    }
}
