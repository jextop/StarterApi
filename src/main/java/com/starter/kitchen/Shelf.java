package com.starter.kitchen;

import com.starter.kitchen.service.RedisService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shelf to put different type food. *
 * Overflow shelf is a virtual one. Kitchen will merge the overflow list.
 *
 * @author ding
 */
public class Shelf {
    public static final String OVERFLOW_FLAG = "overflow";

    private RedisService redisService;
    private String temp;
    private int capacity;

    public Shelf(RedisService redisService, String temp, int capacity) {
        this.redisService = redisService;
        this.temp = temp;
        this.capacity = capacity;
    }

    public String getTemp() {
        return temp;
    }

    public boolean isOverflowed() {
        return redisService.hSize(temp) > capacity;
    }

    public int getOverflowCount() {
        return Math.max(0, (int) redisService.hSize(temp) - capacity);
    }

    public void addOrder(Order order) {
        redisService.hSet(temp, order.getId(), order);
    }

    public void removeOrder(Order order) {
        redisService.hDelKey(temp, order.getId());
    }

    public List<Order> cleanOrders() {
        // Remember the removed order
        List<Order> removedOrders = new ArrayList<>();

        // Loop the sorted orders
        List<Order> orderList = getOrders();
        for (Order order : orderList) {
            if (order.getValue() <= 0) {
                removeOrder(order);
                removedOrders.add(order);
            } else {
                // Orders are sorted by value
                break;
            }
        }

        return removedOrders;
    }

    public Order getOrderWithMinLife() {
        Map<String, Order> orderMap = redisService.hGet(temp);
        List<Order> orders = new ArrayList<>(orderMap.values());

        // Sort by time to zero value
        orders.sort(Comparator.comparing(Order::getLife));
        return CollectionUtils.isEmpty(orders) ? null : orders.get(0);
    }

    public Order getOrderWithMinValue() {
        List<Order> orders = getOrders();
        return CollectionUtils.isEmpty(orders) ? null : orders.get(0);
    }

    public List<Order> getOrders() {
        Map<String, Order> orderMap = redisService.hGet(temp);
        List<Order> orders = new ArrayList<>(orderMap.values());

        // Sort by value
        orders.sort(Comparator.comparing(Order::getValue));
        return orders;
    }

    public Map<String, Object> getInfo() {
        List<Order> orders = getOrders();

        // Virtual overflow shelf
        List<Order> overflowOrders = null;
        if (orders.size() > capacity) {
            overflowOrders = orders.subList(capacity, orders.size());
            orders = orders.subList(0, capacity);
        }

        Map<String, Object> map = new HashMap<>(3);
        map.put("temp", temp);
        map.put("items", orders);
        map.put(OVERFLOW_FLAG, overflowOrders);
        return map;
    }
}
