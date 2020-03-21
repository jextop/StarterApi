package com.starter.mq;

import com.common.util.LogUtil;
import org.apache.activemq.command.ActiveMQQueue;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.Topic;
import java.util.Map;

/**
 * @author ding
 */
@Component
public class MqConsumer {
    private static final String QUEUE = "starter.process";
    private static final String TOPIC = "starter.status";

    @Bean
    public Queue queue() {
        return new ActiveMQQueue(QUEUE);
    }

    @Bean
    public Topic topic() {
        return new ActiveMQTopic(TOPIC);
    }

//    @JmsListener(destination = QUEUE)
//    public void listenQueue(Message msg) throws JMSException {
//        Map<String, ?> msgMap = MqUtil.parseMsg(msg);
//        LogUtil.info("(Queue) Receive process msg", msgMap);
//    }

    @JmsListener(destination = TOPIC, containerFactory = "jmsTopicListenerContainerFactory")
    public void listenTopic(Message msg) throws JMSException {
        Map<String, ?> msgMap = MqUtil.parseMsg(msg);
        LogUtil.info("(Topic) Receive status msg", msgMap);
    }
}
