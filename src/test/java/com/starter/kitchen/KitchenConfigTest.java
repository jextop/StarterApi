package com.starter.kitchen;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KitchenConfigTest {
    @Autowired
    KitchenConfig kitchenConfig;

    @Test
    public void testConfig() {
        // To keep 100% unit-testing coverage
        Assertions.assertNotNull(kitchenConfig.toString());
        Assertions.assertTrue(kitchenConfig.getOverflow() >= 0);
    }
}
