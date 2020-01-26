package com.starter.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {
    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @SuppressWarnings("all")
    @Resource(name = "stringRedisTemplate")
    ValueOperations<String, String> strOps;

    @Autowired
    RedisTemplate<Object, Object> redisTemplate;

    @SuppressWarnings("all")
    @Resource(name = "redisTemplate")
    ValueOperations<Object, Object> valOps;

    @SuppressWarnings("all")
    @Resource(name = "redisTemplate")
    ListOperations<Object, Object> listOps;

    @SuppressWarnings("all")
    @Resource(name = "redisTemplate")
    HashOperations<Object, Object, Object> hashOps;

    @SuppressWarnings("all")
    @Resource(name = "redisTemplate")
    SetOperations<Object, Object> setOps;

    public long incr(String key) {
        Long ret = strOps.increment(key, 1L);
        return ret == null ? 0 : ret;
    }

    public boolean expire(String key, long seconds) {
        Boolean ret = stringRedisTemplate.expire(key, seconds, TimeUnit.SECONDS);
        return ret == null ? false : ret;
    }

    public long getExpire(String key) {
        Long ret = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        return ret == null ? 0 : ret;
    }

    public void del(Object key) {
        redisTemplate.delete(key);
    }

    /**
     * String operation
     */
    public void delStr(String key) {
        stringRedisTemplate.delete(key);
    }

    public String getStr(String key) {
        return strOps.get(key);
    }

    public void setStr(String key, String v) {
        strOps.set(key, v);
    }

    public void setStr(String key, String v, long seconds) {
        strOps.set(key, v, seconds, TimeUnit.SECONDS);
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
    public Object get(Object key) {
        return valOps.get(key);
    }

    public void set(Object key, Object v) {
        valOps.set(key, v);
    }

    public void set(Object key, Object v, long seconds) {
        valOps.set(key, v, seconds, TimeUnit.SECONDS);
    }

    public void set1Minute(Object key, Object v) {
        set(key, v, 60);
    }

    public void set5Minutes(Object key, Object v) {
        set(key, v, 60 * 5);
    }

    public void set1Hour(Object key, Object v) {
        set(key, v, 3600);
    }

    public void set1Day(Object key, Object v) {
        set(key, v, 3600 * 24);
    }

    public void set1Week(Object key, Object v) {
        set(key, v, 3600 * 24 * 7);
    }

    public void set1Month(Object key, Object v) {
        set(key, v, 3600 * 24 * 30);
    }

    /**
     * List operation
     */
    public long lSize(String key) {
        Long size = listOps.size(key);
        return size == null ? 0 : size;
    }

    public Object lPop(String key) {
        return listOps.leftPop(key);
    }

    /**
     * index >=0 时，0 表头，1 第二个元素，依次类推
     * index <0 时，-1，表尾，-2倒数第二个元素，依次类推
     */
    public void lTrim(String key, long start, long end) {
        listOps.trim(key, start, end);
    }

    /**
     * 0 到 -1 代表所有值
     */
    public List<Object> lGet(String key) {
        return lGet(key, 0, -1);
    }

    public List<Object> lGet(String key, long start, long end) {
        List<Object> list = listOps.range(key, start, end);
        return list == null ? new ArrayList<Object>() : list;
    }

    public boolean lSet(String key, Object value) {
        Long count = listOps.rightPush(key, value);
        return count != null && count > 0;
    }

    public boolean lSet(String key, List<Object> values) {
        Long count = listOps.rightPushAll(key, values);
        return count != null && count > 0;
    }

    /**
     * HashMap operation
     */
    public void hDelKey(String key, Object item) {
        hashOps.delete(key, item);
    }

    public boolean hHasKey(String key, Object item) {
        Boolean ret = hashOps.hasKey(key, item);
        return ret == null ? false : ret;
    }

    public Object hGet(String key, Object item) {
        return hashOps.get(key, item);
    }

    public Map<Object, Object> hGet(String key) {
        return hashOps.entries(key);
    }

    public void hSet(String key, Object item, Object value) {
        hashOps.put(key, item, value);
    }

    public void hSet(String key, Map<Object, Object> map) {
        hashOps.putAll(key, map);
    }

    /**
     * Set operation
     */
    public long sSize(String key) {
        Long ret = setOps.size(key);
        return ret == null ? 0 : ret;
    }

    public long sDelValue(String key, Object... values) {
        Long ret = setOps.remove(key, values);
        return ret == null ? 0 : ret;
    }

    public boolean sHasValue(String key, Object value) {
        Boolean ret = setOps.isMember(key, value);
        return ret == null ? false : ret;
    }

    public Set<Object> sGet(String key) {
        return setOps.members(key);
    }

    public long sSet(String key, Object... values) {
        Long ret = setOps.add(key, values);
        return ret == null ? 0 : ret;
    }
}
