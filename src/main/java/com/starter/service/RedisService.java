package com.starter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @SuppressWarnings("all")
    @Resource(name = "stringRedisTemplate")
    ValueOperations<String, String> strValOps;

    @Autowired
    RedisTemplate<Object, Object> redisTemplate;

    @SuppressWarnings("all")
    @Resource(name = "redisTemplate")
    ValueOperations<Object, Object> objValOps;

    @SuppressWarnings("all")
    @Resource(name = "redisTemplate")
    ListOperations<Object, Object> listOps;

    public long incr(String key) {
        Long ret = strValOps.increment(key, 1L);
        return ret == null ? 0 : ret;
    }

    public boolean expire(String key, long seconds) {
        Boolean ret = stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
        return ret == null ? false : ret;
    }

    /**
     * List operation
     */
    public long listSize(String key) {
        Long size = listOps.size(key);
        return size == null ? 0 : size;
    }

    public boolean listPush(String key, Object value) {
        Long count = listOps.rightPush(key, value);
        return count != null && count > 0;
    }

    public Object listPop(String key) {
        return listOps.leftPop(key);
    }

    /**
     * index >=0 时，0 表头，1 第二个元素，依次类推
     * index <0 时，-1，表尾，-2倒数第二个元素，依次类推
     */
    public void trimList(String key, long start, long end) {
        listOps.trim(key, start, end);
    }

    public void delList(String key) {
        listOps.trim(key, -1, 0);
    }

    /**
     * 0 到 -1 代表所有值
     */
    public List<Object> getList(String key) {
        return getList(key, 0, -1);
    }

    public List<Object> getList(String key, long start, long end) {
        List<Object> list = listOps.range(key, start, end);
        return list == null ? new ArrayList<Object>() : list;
    }

    /**
     * String operation
     */
    public void delStr(String key) {
        stringRedisTemplate.delete(key);
    }

    public String getStr(String key) {
        return strValOps.get(key);
    }

    public void setStr(String key, String v) {
        strValOps.set(key, v);
    }

    public void setStr(String key, String v, long seconds) {
        strValOps.set(key, v, seconds, TimeUnit.SECONDS);
    }

    public void setStr1Minute(String key, String v) {
        setStr(key, v, 60);
    }

    public void setStr5Minutes(String key, String v) {
        setStr(key, v, 60 * 5);
    }

    public void setStr1Hour(String key, String v) {
        setStr(key, v, 3600);
    }

    public void setStr1Day(String key, String v) {
        setStr(key, v, 3600 * 24);
    }

    public void setStr1Week(String key, String v) {
        setStr(key, v, 3600 * 24 * 7);
    }

    public void setStr1Month(String key, String v) {
        setStr(key, v, 3600 * 24 * 30);
    }

    /**
     * Object operation
     */
    public void delObj(Object key) {
        redisTemplate.delete(key);
    }

    public Object getObj(Object key) {
        return objValOps.get(key);
    }

    public void setObj(Object key, Object v) {
        objValOps.set(key, v);
    }

    public void setObj(Object key, Object v, long seconds) {
        objValOps.set(key, v, seconds, TimeUnit.SECONDS);
    }

    public void setObj1Minute(Object key, Object v) {
        setObj(key, v, 60);
    }

    public void setObj5Minutes(Object key, Object v) {
        setObj(key, v, 60 * 5);
    }

    public void setObj1Hour(Object key, Object v) {
        setObj(key, v, 3600);
    }

    public void setObj1Day(Object key, Object v) {
        setObj(key, v, 3600 * 24);
    }

    public void setObj1Week(Object key, Object v) {
        setObj(key, v, 3600 * 24 * 7);
    }

    public void setObj1Month(Object key, Object v) {
        setObj(key, v, 3600 * 24 * 30);
    }
}
