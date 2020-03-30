package com.starter.mq;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.messaging.MessagingException;

import javax.jms.Destination;
import java.util.Date;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MqServiceTest {
    @Mock
    JmsMessagingTemplate template;

    @Autowired
    MqService mqService;

    @BeforeEach
    public void initMock() {
        mqService.jmsMessagingTemplate = template;
    }

    @Test
    public void testSendQueue() {
        mqService.sendQueue(new HashMap<String, Object>() {{
            put("msg", "test active queue from java");
            put("date", new Date().toString());
        }});
        verify(template, atLeastOnce()).convertAndSend(any(Destination.class), anyString());
    }

    @Test
    public void testSendTopic() {
        mqService.sendTopic(new HashMap<String, Object>() {{
            put("msg", "test active topic from java");
            put("date", new Date().toString());
        }});
        verify(template, atLeastOnce()).convertAndSend(any(Destination.class), anyString());
    }

    @Test
    public void testSendMessage() {
        // Mock
        Destination destination = mock(Destination.class);
        Object msg = mock(Object.class);

        // Stub
        doNothing().when(template).convertAndSend(any(Destination.class), anyString());

        // Verify
        mqService.sendMessage(destination, msg);
        verify(template, atLeastOnce()).convertAndSend(any(Destination.class), anyString());

        // Exception
        doThrow(new MessagingException("unit testing mock")).when(template)
                .convertAndSend(any(Destination.class), anyString());

        mqService.sendMessage(destination, msg);
        verify(template, atLeast(2)).convertAndSend(any(Destination.class), anyString());
    }
}
