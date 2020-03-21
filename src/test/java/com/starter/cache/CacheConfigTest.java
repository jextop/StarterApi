package com.starter.cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static org.mockito.Mockito.mock;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CacheConfigTest {
    @Test
    public void test() {
        CacheConfig config = new CacheConfig();
        config.factory = mock(RedisConnectionFactory.class);

        Assertions.assertNotNull(config.redisTemplate());
    }
}
