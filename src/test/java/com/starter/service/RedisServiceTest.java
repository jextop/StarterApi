package com.starter.service;

import com.common.util.LogUtil;
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

@SpringBootTest
public class RedisServiceTest {
    @Autowired
    RedisService redisService;

    @Test
    public void testIncr() {
        String key = "RedisServiceTest.testIncr";
        long ret = redisService.incr(key);
        LogUtil.info(ret);
        Assertions.assertTrue(ret > 0);

        LogUtil.info(redisService.getExpire(key));
        redisService.expire(key, 1);
    }

    @Test
    public void testStr() {
        final String key = "RedisServiceTest.testStr";
        LogUtil.info("String before set", redisService.getStr(key));

        final String str = key + ": " + new Date().toString();
        redisService.setStr(key, str);

        final String newStr = redisService.getStr(key);
        LogUtil.info("New String value", newStr);
        Assertions.assertEquals(str, newStr);

        redisService.delStr(key);
        final String delStr = redisService.getStr(key);
        LogUtil.info("String after del", delStr);
        Assertions.assertNull(delStr);
    }

    @Test
    public void testVal() {
        final Object key = new Date();
        LogUtil.info("Obj before set", redisService.get(key));

        final Object obj = new Date();
        redisService.set(key, obj);

        final Object newObj = redisService.get(key);
        LogUtil.info("New obj value", newObj);
        Assertions.assertEquals(obj, newObj);

        redisService.del(key);
        final Object delObj = redisService.get(key);
        LogUtil.info("Obj after del", delObj);
        Assertions.assertNull(delObj);
    }

    @Test
    public void testList() {
        String key = "RedisServiceTest.testList";
        redisService.del(key);
        Assertions.assertTrue(redisService.lGet(key).isEmpty());

        redisService.lSet(key, 1);
        redisService.lSet(key, 2);
        redisService.lSet(key, 3);
        redisService.lPop(key);
        redisService.lSet(key, new ArrayList<Object>() {{
            add(4);
            add(5);
        }});
        LogUtil.info(redisService.lSize(key));

        List<Object> list = redisService.lGet(key);
        LogUtil.info(list);
        Assertions.assertEquals(list, Arrays.asList(new Object[]{2, 3, 4, 5}));

        redisService.lTrim(key, 1,-2);
        list = redisService.lGet(key);
        LogUtil.info(list);
        Assertions.assertEquals(list, Arrays.asList(new Object[]{3, 4}));

        redisService.del(key);
        Assertions.assertTrue(redisService.lGet(key).isEmpty());
    }

    @Test
    public void testHash() {
        String key = "RedisServiceTest.testHash";
        redisService.del(key);
        Assertions.assertTrue(redisService.hGet(key).isEmpty());

        redisService.hSet(key, 1, 10);
        redisService.hSet(key, 2, 20);
        redisService.hSet(key, 3, 30);
        redisService.hDelKey(key, 1);
        redisService.hSet(key, new HashMap<Object, Object>(){{
            put(4, 40);
            put(5, 50);
        }});

        Map<Object, Object> ret = redisService.hGet(key);
        LogUtil.info(ret);
        Assertions.assertFalse(ret.isEmpty());

        LogUtil.info(redisService.hGet(key, 1));
        LogUtil.info(redisService.hGet(key, 4));
        Assertions.assertFalse(redisService.hGet(key, 1) != null);
        Assertions.assertTrue(redisService.hHasKey(key, 4));
        redisService.del(key);
    }

    @Test
    public void testSet() {
        String key = "RedisServiceTest.testSet";
        redisService.del(key);
        Assertions.assertTrue(redisService.sGet(key).isEmpty());

        redisService.sSet(key, 1, 2, 3);
        redisService.sDelValue(key, 1, 2);
        redisService.sSet(key, 4);

        Set<Object> ret = redisService.sGet(key);
        LogUtil.info(ret);
        Assertions.assertFalse(ret.isEmpty());

        Assertions.assertFalse(redisService.sHasValue(key, 2));
        Assertions.assertTrue(redisService.sHasValue(key, 4));
        Assertions.assertTrue(redisService.sSize(key) == 2);
        redisService.del(key);
    }
}
