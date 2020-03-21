package com.starter.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FastJsonConverterTest {
    @Test
    public void test() {
        FastJsonConverter fastJsonConverter = new FastJsonConverter();
        Assertions.assertNotNull(fastJsonConverter.fastJsonHttpMessageConverters());
    }
}
