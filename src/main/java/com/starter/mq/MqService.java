package com.starter.mq;

import com.common.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import javax.jms.Destination;
import javax.jms.Queue;
import javax.jms.Topic;
import java.util.Map;

/**
 * @author ding
 */
@Service
public class MqService {
    JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    Queue queue;

    @Autowired
    Topic topic;

    @Autowired
    public MqService(JmsMessagingTemplate jmsMessagingTemplate) {
        this.jmsMessagingTemplate = jmsMessagingTemplate;
    }

    public void sendQueue(Map<String, Object> msgMap) {
        sendMessage(queue, msgMap);
    }

    public void sendTopic(Map<String, Object> msgMap) {
        sendMessage(topic, msgMap);
    }

    public void sendMessage(Destination dest, Object msg) {
        String msgStr = MqUtil.formatMsg(msg);
        LogUtil.info(dest.getClass().getSimpleName(), "Send message", msgStr);

        try {
            jmsMessagingTemplate.convertAndSend(dest, msgStr);
        } catch (MessagingException e) {
            LogUtil.error(e.getMessage());
        }
    }
}
