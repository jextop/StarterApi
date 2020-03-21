package com.starter.cache;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class FastJsonSerializerTest {
    @Test
    public void testFastJsonSerializer() {
        FastJsonSerializer<Object> fastJsonSerializer = new FastJsonSerializer<>(Object.class);

        // data
        Map<String, Object> item = new HashMap<String, Object>() {{
            put("k", "v");
        }};

        // Serialize and deserialize
        byte[] data = fastJsonSerializer.serialize(item);
        Object obj = fastJsonSerializer.deserialize(data);

        // Verify
        Assertions.assertTrue(obj instanceof Map);
        Assertions.assertEquals(item, obj);

        // Invalid data
        Assertions.assertEquals(0, fastJsonSerializer.serialize(null).length);
        Assertions.assertEquals(null, fastJsonSerializer.deserialize(new byte[0]));
    }
}
