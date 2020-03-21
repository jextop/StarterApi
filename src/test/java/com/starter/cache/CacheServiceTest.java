package com.starter.cache;

import com.common.util.LogUtil;
import com.starter.kitchen.Order;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CacheServiceTest {
    @Autowired
    CacheService cacheService;

    @Test
    public void testIncr() {
        String key = "RedisServiceTest.testIncr";
        long ret = cacheService.incr(key);
        LogUtil.info(ret);
        Assertions.assertTrue(ret > 0);
        Assertions.assertTrue(cacheService.getExpire(key) < 0);
    }

    @Test
    public void testExpire() {
        String key = "RedisServiceTest.testExpire";
        cacheService.setStr(key, key);
        Assertions.assertTrue(cacheService.getExpire(key) < 0);

        int seconds = 2;
        cacheService.expire(key, seconds);
        Assertions.assertEquals(seconds, cacheService.getExpire(key), 1);
    }

    @Test
    public void testStr() {
        final String key = "RedisServiceTest.testStr";
        LogUtil.info("String before set", cacheService.getStr(key));

        final String str = key + ": " + new Date().toString();
        cacheService.setStr(key, str);

        final String newStr = cacheService.getStr(key);
        LogUtil.info("New String value", newStr);
        Assertions.assertEquals(str, newStr);

        cacheService.delStr(key);
        final String delStr = cacheService.getStr(key);
        LogUtil.info("String after del", delStr);
        Assertions.assertNull(delStr);
    }

    @Test
    public void testValue() {
        String key = new Date().toString();
        LogUtil.info("Obj before set", cacheService.get(key));

        Date obj = new Date();
        cacheService.set(key, obj);

        Object newObj = cacheService.get(key);
        LogUtil.info("New obj value", newObj);
        Assertions.assertEquals(obj.getTime(), newObj);

        cacheService.del(key);
        Object delObj = cacheService.get(key);
        LogUtil.info("Obj after del", delObj);
        Assertions.assertNull(delObj);
    }

    @Test
    public void testList() {
        String key = "RedisServiceTest.testList";
        cacheService.del(key);
        Assertions.assertTrue(cacheService.lGet(key).isEmpty());

        cacheService.lSet(key, 1);
        cacheService.lSet(key, 2);
        cacheService.lSet(key, 3);
        cacheService.lPop(key);
        cacheService.lSet(key, new ArrayList<Object>() {{
            add(4);
            add(5);
        }});
        LogUtil.info(cacheService.lSize(key));

        List<Object> list = cacheService.lGet(key);
        LogUtil.info(list);
        Assertions.assertEquals(list, Arrays.asList(new Object[]{2, 3, 4, 5}));

        cacheService.lTrim(key, 1, -2);
        list = cacheService.lGet(key);
        LogUtil.info(list);
        Assertions.assertEquals(list, Arrays.asList(new Object[]{3, 4}));

        cacheService.del(key);
        Assertions.assertTrue(cacheService.lGet(key).isEmpty());
    }

    @Test
    public void testHash() {
        // Prepare
        String key = "RedisServiceTest.testHash";
        cacheService.del(key);
        Assertions.assertTrue(cacheService.hGet(key).isEmpty());

        // Data
        cacheService.hSet(key, "1", 10);
        cacheService.hSet(key, "2", 20);
        cacheService.hSet(key, "3", 30);
        cacheService.hDelKey(key, "1");

        cacheService.hSet(key, new HashMap<String, Object>() {{
            put("4", 40);
            put("5", 50);
        }});

        // Read
        Map<String, Object> ret = cacheService.hGet(key);
        Assertions.assertEquals(4, ret.size());
        Assertions.assertEquals(4, cacheService.hSize(key));

        Assertions.assertNull(cacheService.hGet(key, "1"));
        Assertions.assertTrue(cacheService.hHasKey(key, "4"));
        cacheService.del(key);
    }

    @Test
    public void testSet() {
        String key = "RedisServiceTest.testSet";
        cacheService.del(key);
        Assertions.assertTrue(cacheService.sGet(key).isEmpty());

        cacheService.sSet(key, 1, 2, 3);
        cacheService.sDelValue(key, 1, 2);
        cacheService.sSet(key, 4);

        Set<Object> ret = cacheService.sGet(key);
        LogUtil.info(ret);
        Assertions.assertFalse(ret.isEmpty());

        Assertions.assertFalse(cacheService.sHasValue(key, 2));
        Assertions.assertTrue(cacheService.sHasValue(key, 4));
        Assertions.assertTrue(cacheService.sSize(key) == 2);
        cacheService.del(key);
    }
}
