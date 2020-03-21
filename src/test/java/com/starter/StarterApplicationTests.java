package com.starter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.PortInUseException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class StarterApplicationTests {
    @Test
    void contextLoads() {
    }

    @Test
    void testMain() {
        try {
            StarterApplication.main(new String[]{"--server.port=8091"});
        } catch (PortInUseException e) {
        }
    }
}
