package com.starter.mq;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.HashMap;

@SpringBootTest
public class MqServiceTest {
    @Autowired
    MqService mqService;

    @Test
    public void testSendQueue() {
        mqService.sendQueue(new HashMap<String, Object>() {{
            put("msg", "test active queue from java");
            put("date", new Date().toString());
        }});
    }

    @Test
    public void testSendTopic() {
        mqService.sendTopic(new HashMap<String, Object>() {{
            put("msg", "test active topic from java");
            put("date", new Date().toString());
        }});
    }
}
