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
    public void testObj() {
        final Object key = new Date();
        LogUtil.info("Obj before set", redisService.getObj(key));

        final Object obj = new Date();
        redisService.setObj(key, obj);

        final Object newObj = redisService.getObj(key);
        LogUtil.info("New obj value", newObj);
        Assertions.assertEquals(obj, newObj);

        redisService.del(key);
        final Object delObj = redisService.getObj(key);
        LogUtil.info("Obj after del", delObj);
        Assertions.assertNull(delObj);
    }

    @Test
    public void testList() {
        String key = "RedisServiceTest.testList";
        redisService.del(key);
        Assertions.assertTrue(redisService.getList(key).isEmpty());

        redisService.pushList(key, 1);
        redisService.pushList(key, 2);
        redisService.pushList(key, 3);
        redisService.popList(key);
        redisService.pushList(key, new ArrayList<Object>() {{
            add(4);
            add(5);
        }});
        LogUtil.info(redisService.listSize(key));

        List<Object> list = redisService.getList(key);
        LogUtil.info(list);
        Assertions.assertEquals(list, Arrays.asList(new Object[]{2, 3, 4, 5}));

        redisService.trimList(key, 1,-2);
        list = redisService.getList(key);
        LogUtil.info(list);
        Assertions.assertEquals(list, Arrays.asList(new Object[]{3, 4}));

        redisService.del(key);
        Assertions.assertTrue(redisService.getList(key).isEmpty());
    }

    @Test
    public void testHash() {
        String key = "RedisServiceTest.testHash";
        redisService.del(key);
        Assertions.assertTrue(redisService.getHash(key).isEmpty());

        redisService.setHash(key, 1, 10);
        redisService.setHash(key, 2, 20);
        redisService.setHash(key, 3, 30);
        redisService.delHashKey(key, 1);
        redisService.setHash(key, new HashMap<Object, Object>(){{
            put(4, 40);
            put(5, 50);
        }});

        Map<Object, Object> ret = redisService.getHash(key);
        LogUtil.info(ret);
        Assertions.assertFalse(ret.isEmpty());

        LogUtil.info(redisService.getHash(key, 1));
        LogUtil.info(redisService.getHash(key, 4));
        Assertions.assertFalse(redisService.getHash(key, 1) != null);
        Assertions.assertTrue(redisService.hasHashKey(key, 4));
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
