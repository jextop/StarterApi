package com.starter.mq;

import com.common.util.LogUtil;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import java.util.Map;

@Component
public class MqConsumer {
//    @JmsListener(destination = "starter.process")
//    public void listenQueue(Message msg) {
//        Map<String, ?> msgMap = MqUtil.parseMsg(msg);
//        LogUtil.info("Receive process msg", msgMap);
//    }

    @JmsListener(destination = "starter.status", containerFactory = "jmsTopicListenerContainerFactory")
    public void listenTopic(Message msg) {
        Map<String, ?> msgMap = MqUtil.parseMsg(msg);
        LogUtil.info("Receive status msg", msgMap);
    }
}
