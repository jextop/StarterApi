package com.starter.kitchen;

import java.io.Serializable;

/**
 * @author ding
 */
public class Order implements Serializable, Comparable<Order> {
    private static final long serialVersionUID = 1L;

    private String name;
    private String temp;
    private int shelfLife;
    private double decayRate;

    private String id;
    private String status;
    private long cookTime;
    private long pickupTime;

    public Order() {
        shelfLife = 300;
    }

    public double getPickup() {
        return pickupTime <= 0 ? 0 : (pickupTime - cookTime) / 1000;
    }

    public double getPickupValue() {
        long orderAge = (pickupTime - cookTime) / 1000;
        return pickupTime <= 0 ? 0 : (shelfLife - orderAge - decayRate * orderAge);
    }

    public double getValue() {
        long orderAge = (System.currentTimeMillis() - cookTime) / 1000;
        return Math.max(0, shelfLife - orderAge - decayRate * orderAge);
    }

    public double getNormalizedValue() {
        return shelfLife == 0 ? 0 : (getValue() / shelfLife);
    }

    public double getLife() {
        return getValue() / (1 + decayRate);
    }

    @Override
    public int compareTo(Order o) {
        return Double.compare(this.getValue(), o.getValue());
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Order && id.equals(((Order) obj).id);
    }

    @Override
    public String toString() {
        return "Order{" +
                "name='" + name + '\'' +
                ", temp='" + temp + '\'' +
                ", shelfLife=" + shelfLife +
                ", decayRate=" + decayRate +
                ", id='" + id + '\'' +
                ", status=" + status +
                ", cookTime=" + cookTime +
                ", pickupTime=" + pickupTime +
                ", pickup=" + getPickup() +
                ", pickupValue=" + getPickupValue() +
                ", value=" + getValue() +
                ", normalizedValue=" + getNormalizedValue() +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public int getShelfLife() {
        return shelfLife;
    }

    public void setShelfLife(int shelfLife) {
        this.shelfLife = shelfLife;
    }

    public double getDecayRate() {
        return decayRate;
    }

    public void setDecayRate(double decayRate) {
        this.decayRate = decayRate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCookTime() {
        return cookTime;
    }

    public void setCookTime(long cookTime) {
        this.cookTime = cookTime;
    }

    public long getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(long pickupTime) {
        this.pickupTime = pickupTime;
    }
}
