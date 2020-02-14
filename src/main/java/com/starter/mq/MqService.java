package com.starter.mq;

import com.common.util.LogUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

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
        String msgStr = MqUtil.formatMsg(msgMap);
        LogUtil.info("Send queue msg", msgStr);

        try {
            jmsMessagingTemplate.convertAndSend(queue, msgStr);
        } catch (MessagingException e) {
            LogUtil.error(e.getMessage());
            throw e;
        }
    }

    public void sendTopic(Map<String, ?> msgMap) {
        String msgStr = MqUtil.formatMsg(msgMap);
        LogUtil.info("Send topic msg", msgStr);

        try {
            jmsMessagingTemplate.convertAndSend(topic, msgStr);
        } catch (MessagingException e) {
            LogUtil.error(e.getMessage());
            throw e;
        }
    }
}
