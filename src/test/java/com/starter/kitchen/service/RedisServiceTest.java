package com.starter.kitchen.service;

import com.starter.kitchen.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RedisServiceTest {
    @Autowired
    RedisService redisService;

    @Test
    public void testExpire() {
        String key = "RedisServiceTest.testExpire";
        redisService.hSet(key, "1", new Order() {{ setId("10"); }});
        Assertions.assertTrue(redisService.getExpire(key) < 0);

        int seconds = 3;
        redisService.expire(key, seconds);
        Assertions.assertEquals(seconds, redisService.getExpire(key), 1);
    }

    @Test
    public void testHash() {
        // Empty
        String key = "RedisServiceTest.testHashOperation";
        Assertions.assertTrue(redisService.hGet(key).isEmpty());

        // Data
        redisService.hSet(key, "1", new Order() {{ setId("10"); }});
        redisService.hSet(key, "2", new Order() {{ setId("20"); }});
        redisService.hSet(key, "3", new Order() {{ setId("30"); }});
        redisService.hDelKey(key, "1");

        redisService.hSet(key, new HashMap<String, Order>() {{
            put("4", new Order() {{ setId("40"); }});
            put("5", new Order() {{ setId("50"); }});
        }});

        // Read
        Map<String, Order> ret = redisService.hGet(key);
        Assertions.assertEquals(4, ret.size());
        Assertions.assertEquals(4, redisService.hSize(key));

        Assertions.assertNull(redisService.hGet(key, "1"));
        Assertions.assertTrue(redisService.hHasKey(key, "4"));
        redisService.del(key);
    }
}
