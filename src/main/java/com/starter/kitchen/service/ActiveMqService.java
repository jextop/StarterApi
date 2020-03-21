package com.starter.kitchen.service;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import javax.jms.Destination;

/**
 * @author ding
 */
@Service
public class ActiveMqService {
    private JmsMessagingTemplate jmsMessagingTemplate;

    @Autowired
    public ActiveMqService(JmsMessagingTemplate jmsMessagingTemplate) {
        this.jmsMessagingTemplate = jmsMessagingTemplate;
    }

    public void sendMessage(Destination dest, Object msg) {
        String msgStr = JSON.toJSONString(msg);

        try {
            jmsMessagingTemplate.convertAndSend(dest, msgStr);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
