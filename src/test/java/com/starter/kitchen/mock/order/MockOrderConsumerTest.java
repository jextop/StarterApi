package com.starter.kitchen.mock.order;

import com.alibaba.fastjson.JSON;
import com.common.util.CodeUtil;
import com.starter.kitchen.Order;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockOrderConsumerTest {
    @Autowired
    MockOrderConsumer orderConsumer;

    @Test
    public void testOrderStatus() {
        Assertions.assertNotNull(orderConsumer.orderStatus());
    }

    @Test
    public void testListenTopic() throws JMSException {
        Order order = new Order() {{
            setId(CodeUtil.getCode());
        }};

        // Mock
        TextMessage msg = mock(TextMessage.class);
        when(msg.getText()).thenReturn(JSON.toJSONString(order));

        // Send msg
        orderConsumer.listenTopic(msg);
    }
}
