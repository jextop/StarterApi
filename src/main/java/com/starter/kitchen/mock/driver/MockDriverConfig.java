package com.starter.kitchen.mock.driver;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author ding
 */
@Configuration
@ConfigurationProperties(prefix = "kitchen.mock.driver")
public class MockDriverConfig {
    private int min;
    private int max;
    private boolean auto;

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public int getInterval() {
        // Generate a random interval for driver to arrive
        return (int) (Math.random() * (max - min)) + min;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }
}
