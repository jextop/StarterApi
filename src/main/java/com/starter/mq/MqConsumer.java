package com.starter.mq;

import com.common.util.LogUtil;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import java.util.Map;

@Component
public class MqConsumer {
    public static final String QUEUE = "starter.process";
    public static final String TOPIC = "starter.status";

//    @JmsListener(destination = QUEUE)
//    public void listenQueue(Message msg) {
//        Map<String, ?> msgMap = MqUtil.parseMsg(msg);
//        LogUtil.info("(Queue) Receive process msg", msgMap);
//    }

    @JmsListener(destination = TOPIC, containerFactory = "jmsTopicListenerContainerFactory")
    public void listenTopic(Message msg) {
        Map<String, ?> msgMap = MqUtil.parseMsg(msg);
        LogUtil.info("(Topic) Receive status msg", msgMap);
    }
}
