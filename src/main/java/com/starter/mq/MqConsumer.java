package com.starter.mq;

import com.common.util.LogUtil;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import java.util.Map;

@Component
public class MqConsumer {
    @JmsListener(destination = "starter.queue")
    public void listenQueue(Message msg) {
        Map<String, ?> msgMap = MqUtil.parseMsg(msg);
        LogUtil.info("Receive queue msg", msgMap);
    }

    @JmsListener(destination = "starter.topic", containerFactory = "jmsTopicListenerContainerFactory")
    public void listenTopic(Message msg) {
        Map<String, ?> msgMap = MqUtil.parseMsg(msg);
        LogUtil.info("Receive topic msg", msgMap);
    }
}
