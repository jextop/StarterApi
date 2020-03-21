package com.starter.kitchen.service;

import com.starter.kitchen.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author ding
 */
@Service
public class RedisService {
    @Autowired
    @Qualifier("orderRedisTemplate")
    RedisTemplate<String, Order> orderRedisTemplate;

    @SuppressWarnings("all")
    @Resource(name = "orderRedisTemplate")
    HashOperations<String, String, Order> hashOps;

    public boolean expire(String key, long seconds) {
        Boolean ret = orderRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
        return ret == null ? false : ret;
    }

    public long getExpire(String key) {
        Long ret = orderRedisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ret == null ? 0 : ret;
    }

    public void del(String key) {
        if (orderRedisTemplate.hasKey(key)) {
            orderRedisTemplate.delete(key);
        }
    }

    /**
     * HashMap operations
     */
    public long hSize(String key) {
        Long size = hashOps.size(key);
        return size == null ? 0 : size;
    }

    public void hDelKey(String key, String item) {
        if (hHasKey(key, item)) {
            hashOps.delete(key, item);
        }
    }

    public boolean hHasKey(String key, String item) {
        Boolean ret = hashOps.hasKey(key, item);
        return ret == null ? false : ret;
    }

    public Order hGet(String key, String item) {
        return hashOps.get(key, item);
    }

    public Map<String, Order> hGet(String key) {
        return hashOps.entries(key);
    }

    public void hSet(String key, String item, Order value) {
        hashOps.put(key, item, value);
    }

    public void hSet(String key, Map<String, Order> map) {
        hashOps.putAll(key, map);
    }
}
