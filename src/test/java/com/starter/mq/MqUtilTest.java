package com.starter.mq;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MqUtilTest {
    @Test
    public void testFormatMsg() {
        Map obj = Mockito.mock(Map.class);
        Assertions.assertEquals(JSON.toJSONString(obj), MqUtil.formatMsg(obj));
    }

    @Test
    public void testParseMsg() throws JMSException {
        Map obj = new HashMap<String, Object>();
        String str = JSON.toJSONString(obj);

        // Mock
        TextMessage msg = Mockito.mock(TextMessage.class);

        // Stub
        Mockito.when(msg.getText()).thenReturn(str);

        // Verify
        Map ret = MqUtil.parseMsg(msg);
        Assertions.assertEquals(obj, ret);
        Mockito.verify(msg, Mockito.times(1)).getText();

        // Exception
        Assertions.assertNull(MqUtil.parseMsg(null));
    }
}
