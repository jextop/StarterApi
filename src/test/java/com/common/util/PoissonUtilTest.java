package com.common.util;

import com.starter.StarterApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ding
 */
@SpringBootTest(classes = StarterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PoissonUtilTest {
    @Test
    public void testVariable() {
        double lambda = 3.25;
        int sum = 0, count = 20;
        for (int i = 0; i < count; i++) {
            sum += PoissonUtil.getVariable(lambda);
        }

        double average = 1.0 * sum / count;
        Assertions.assertTrue(Math.abs(average - lambda) < 1);
    }

    @Test
    public void testProbability() {
        double sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += PoissonUtil.getProbability(i, 3.25);
        }
        Assertions.assertTrue(sum > 0.9 && sum < 1);
    }

    @Test
    public void testPoissonUtil() {
        // To keep 100% unit-testing coverage
        Assertions.assertNotNull(new PoissonUtil());
    }
}
