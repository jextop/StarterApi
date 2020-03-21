package com.starter.kitchen;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author ding
 */
@Configuration
@ConfigurationProperties(prefix = "kitchen")
public class KitchenConfig {
    private String[] shelves;
    private int[] capacities;
    private int overflow;

    public String[] getShelves() {
        return shelves;
    }

    public void setShelves(String[] shelves) {
        this.shelves = shelves;
    }

    public int[] getCapacities() {
        return capacities;
    }

    public void setCapacities(int[] capacities) {
        this.capacities = capacities;
    }

    public int getOverflow() {
        return overflow;
    }

    public void setOverflow(int overflow) {
        this.overflow = overflow;
    }
}
