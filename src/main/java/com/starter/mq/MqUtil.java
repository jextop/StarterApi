package com.starter.mq;

import com.common.util.JsonUtil;
import com.common.util.LogUtil;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.Map;

public class MqUtil {
    public static String formatMsg(Map<String, Object> msgMap) {
        // 指定使用字符串格式。Python使用STOMP协议，简单文本格式。
        return JsonUtil.toStr(msgMap);
    }

    public static Map<String, Object> parseMsg(Message msg) {
        // 解析字符消息。Python使用STOMP协议，简单文本格式。
        if (msg instanceof TextMessage) {
            try {
                String msgStr = ((TextMessage) msg).getText();
                return JsonUtil.parseObj(msgStr);
            } catch (JMSException e) {
                LogUtil.error(e.getMessage());
            }
        }

        LogUtil.info("Receive msg", msg.getClass().getSimpleName());
        return null;
    }
}
