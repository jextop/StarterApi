package com.starter.kitchen;

import com.alibaba.fastjson.JSON;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;

/**
 * Message queue for kitchen to receive orders and cook one by one.
 *
 * @author ding
 */
@Component
public class KitchenConsumer {
    private static final String ORDER_QUEUE = "kitchen.order";

    KitchenService kitchenService;

    @Autowired
    public KitchenConsumer(KitchenService kitchenService) {
        this.kitchenService = kitchenService;
    }

    @Bean
    public Queue kitchenOrder() {
        return new ActiveMQQueue(ORDER_QUEUE);
    }

    @JmsListener(destination = ORDER_QUEUE)
    public void listenQueue(Message msg) throws JMSException {
        if (msg instanceof TextMessage) {
            String msgStr = ((TextMessage) msg).getText();
            Order order = JSON.parseObject(msgStr, Order.class);

            kitchenService.cook(order);
        }
    }
}
