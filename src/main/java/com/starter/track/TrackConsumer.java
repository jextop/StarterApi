package com.starter.track;

import com.common.util.LogUtil;
import com.common.util.MapUtil;
import com.starter.mq.MqUtil;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Message;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TrackConsumer {
    public static final String POSITION_TOPIC = "track.position";
    public static final Map<String, Object> trackMap = new ConcurrentHashMap<>();

    @JmsListener(destination = POSITION_TOPIC, containerFactory = "jmsTopicListenerContainerFactory")
    public void listenTopic(Message msg) {
        Map<String, Object> msgMap = MqUtil.parseMsg(msg);
        String uid = MapUtil.getStr(msgMap, "uid");
        LogUtil.info("Receive track position", uid, msgMap);

        // 临时存储前端数据
        trackMap.put(uid, msgMap);

        // todo: 保存历史定位信息

        // todo: 推送数据给后台管理系统
    }
}