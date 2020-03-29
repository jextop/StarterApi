package com.starter.kitchen.service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.messaging.MessagingException;

import javax.jms.Destination;

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
public class ActiveMqServiceTest {
    @Test
    public void testSendMessage() {
        // Mock
        JmsMessagingTemplate template = mock(JmsMessagingTemplate.class);
        ActiveMqService activeMqService = new ActiveMqService(template);

        Destination destination = mock(Destination.class);
        Object msg = mock(Object.class);

        // Stub
        doNothing().when(template).convertAndSend(any(Destination.class), anyString());

        // Verify
        activeMqService.sendMessage(destination, msg);
        verify(template, atLeastOnce()).convertAndSend(any(Destination.class), anyString());

        // Exception
        doThrow(new MessagingException("unit testing mock")).when(template)
                .convertAndSend(any(Destination.class), anyString());

        activeMqService.sendMessage(destination, msg);
        verify(template, atLeast(2)).convertAndSend(any(Destination.class), anyString());
    }
}
