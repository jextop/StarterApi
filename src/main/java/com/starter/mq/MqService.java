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

@Service
public class MqService {
    @Autowired
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    private Queue queue;

    @Autowired
    private Topic topic;

    public void sendQueue(Map<String, ?> msgMap) {
        sendMsg(queue, msgMap);
    }

    public void sendTopic(Map<String, ?> msgMap) {
        sendMsg(topic, msgMap);
    }

    public void sendMsg(Destination dest, Map<String, ?> msgMap) {
        String msgStr = MqUtil.formatMsg(msgMap);
        LogUtil.info(dest.getClass().getSimpleName(), "Send msg", msgStr);

        try {
            jmsMessagingTemplate.convertAndSend(dest, msgStr);
        } catch (MessagingException e) {
            LogUtil.error(e.getMessage());
            throw e;
        }
    }
}
