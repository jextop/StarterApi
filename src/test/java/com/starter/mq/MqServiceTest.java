package com.starter.mq;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
public class MqServiceTest {
    @Autowired
    MqService mqService;

    @Test
    public void testSendQueue() {
        mqService.sendQueue(String.format("test active queue: %s", new Date().toString()));
    }

    @Test
    public void testSendTopic() {
        mqService.sendTopic(String.format("test active topic: %s", new Date().toString()));
    }
}
