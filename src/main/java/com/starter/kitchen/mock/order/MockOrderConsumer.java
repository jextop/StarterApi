package com.starter.kitchen.mock.order;

import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import javax.jms.Topic;

/**
 * Receive order status from kitchen and driver
 *
 * @author ding
 */
@Component
public class MockOrderConsumer {
    private static final String STATUS_TOPIC = "order.status";

    @Bean
    public Topic orderStatus() {
        return new ActiveMQTopic(STATUS_TOPIC);
    }

    @JmsListener(destination = STATUS_TOPIC, containerFactory = "jmsTopicListenerContainerFactory")
    public void listenTopic(Message msg) {
    }
}
