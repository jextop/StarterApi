package com.starter.mq;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.config.JmsListenerContainerFactory;

import javax.jms.ConnectionFactory;

import static org.mockito.Mockito.mock;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MqConfigTest {
    @Autowired
    MqConfig mqConfig;

    @Test
    public void testContainerFactory() {
        ConnectionFactory factory = mock(ConnectionFactory.class);
        JmsListenerContainerFactory containerFactory = mqConfig.jmsTopicListenerContainerFactory(factory);
        Assertions.assertNotNull(containerFactory);
    }
}
