package com.starter.socket;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SocketConfigTest {
    @Autowired
    SocketConfig socketConfig;

    @Test
    public void testLog() {
        Assertions.assertNotNull(socketConfig.serverEndpointExporter());
    }
}
