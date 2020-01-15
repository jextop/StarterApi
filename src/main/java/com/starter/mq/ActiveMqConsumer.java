package com.starter.mq;

import com.common.util.LogUtil;
import com.starter.jext.JextService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class ActiveMqConsumer {
    @Autowired
    JextService jextService;

    @JmsListener(destination = "starter.queue")
    public void listenQueue(String msg) {
        LogUtil.info("Receive queue msg", msg);

        if (JextService.INFO_KEY.equals(msg)) {
            jextService.getInfo(true);
        }
    }

    @JmsListener(destination = "starter.topic", containerFactory = "jmsTopicListenerContainerFactory")
    public void listenTopic(String msg) {
        LogUtil.info("Receive topic msg", msg);
    }
}
