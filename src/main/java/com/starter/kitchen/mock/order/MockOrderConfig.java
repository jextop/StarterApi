package com.starter.kitchen.mock.order;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author ding
 */
@Configuration
@ConfigurationProperties(prefix = "kitchen.mock.order")
public class MockOrderConfig {
    private String cron;
    private double lambda;
    private boolean auto;

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public double getLambda() {
        return lambda;
    }

    public void setLambda(double lambda) {
        this.lambda = lambda;
    }
}
