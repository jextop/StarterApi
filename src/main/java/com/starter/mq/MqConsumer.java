package com.starter.mq;

import com.common.util.LogUtil;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

@Component
public class MqConsumer {
    @JmsListener(destination = "starter.queue")
    public void listenQueue(ActiveMQTextMessage msg) {
        try {
            LogUtil.info("Receive queue msg", msg.getJMSXMimeType(), msg.getText());
        } catch (JMSException e) {
            LogUtil.error(e.getMessage());
        }
    }

    @JmsListener(destination = "starter.topic", containerFactory = "jmsTopicListenerContainerFactory")
    public void listenTopic(ActiveMQTextMessage msg) {
        try {
            LogUtil.info("Receive topic msg", msg.getJMSXMimeType(), msg.getText());
        } catch (JMSException e) {
            LogUtil.error(e.getMessage());
        }
    }
}
