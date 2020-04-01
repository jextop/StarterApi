package com.starter.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

/**
 * @author ding
 */
public class MqUtil {
    public static String formatMsg(Object msg) {
        // Python Stomp发送来的消息使用TextMessage
        return JSON.toJSONString(msg);
    }

    public static JSONObject parseMsg(Message msg) throws JMSException {
        // Python Stomp发送来的消息使用TextMessage
        if (msg instanceof TextMessage) {
            String msgStr = ((TextMessage) msg).getText();
            return JSON.parseObject(msgStr);
        }
        return null;
    }
}
