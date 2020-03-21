package com.starter.kitchen.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static org.mockito.Mockito.mock;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RedisConfigTest {
    @Test
    public void test() {
        RedisConfig redisConfig = new RedisConfig(mock(RedisConnectionFactory.class));

        Assertions.assertNotNull(redisConfig.orderRedisTemplate());
    }
}
