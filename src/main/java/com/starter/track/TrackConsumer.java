package com.starter.track;

import com.common.util.JsonUtil;
import com.common.util.MapUtil;
import com.starter.mq.MqUtil;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Topic;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ding
 */
@Component
public class TrackConsumer {
    private static final String POSITION_TOPIC = "track.position";
    static final Map<String, Object> CLIENT_MAP = new ConcurrentHashMap<>();

    @Bean
    public Topic trackPosition() {
        return new ActiveMQTopic(POSITION_TOPIC);
    }

    @JmsListener(destination = POSITION_TOPIC, containerFactory = "jmsTopicListenerContainerFactory")
    public void listenTopic(Message msg) throws JMSException {
        Map<String, Object> msgMap = MqUtil.parseMsg(msg);

        // 推送数据给后台管理系统
        TrackSocket.sendMessage(JsonUtil.toStr(msgMap));

        // 临时存储位置信息
        String uid = MapUtil.getStr(msgMap, "uid");
        CLIENT_MAP.put(uid, msgMap);

        // todo: 保存历史定位信息
    }
}
