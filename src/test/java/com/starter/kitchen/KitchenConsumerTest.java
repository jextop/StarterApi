package com.starter.kitchen;

import com.alibaba.fastjson.JSON;
import com.common.util.CodeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class KitchenConsumerTest {
    @Autowired
    KitchenConsumer kitchenConsumer;

    @Test
    public void testKitchenOrder() {
        Assertions.assertNotNull(kitchenConsumer.kitchenOrder());
    }

    @Test
    public void testListenQueue() throws JMSException {
        Order order = new Order() {{
            setId(CodeUtil.getCode());
        }};

        // Mock
        TextMessage msg = mock(TextMessage.class);
        when(msg.getText()).thenReturn(JSON.toJSONString(order));

        KitchenService kitchenService = spy(kitchenConsumer.kitchenService);
        kitchenConsumer.kitchenService = kitchenService;
        doNothing().when(kitchenService).cook(any());

        // Send msg
        kitchenConsumer.listenQueue(msg);
        verify(msg, times(1)).getText();
        verify(kitchenService, times(1)).cook(any());
    }
}
