package com.starter.kitchen.mock.order;

import com.alibaba.fastjson.JSON;
import com.common.util.CodeUtil;
import com.common.util.PoissonUtil;
import com.starter.kitchen.Order;
import com.starter.mq.MqService;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.jms.Queue;
import java.io.IOException;
import java.util.List;

/**
 * Mock outer order system to send fake orders.
 *
 * @author ding
 */
@Service
@Configuration
@Lazy
public class MockOrderSystem {
    List<Order> orderList;
    private int index;

    private volatile boolean autoSendOrders;
    private MockOrderConfig orderConfig;

    MqService mqService;
    private Queue kitchenOrder;

    @Autowired
    public MockOrderSystem(
            MockOrderConfig orderConfig,
            MqService mqService,
            Queue kitchenOrder
    ) throws IOException {
        this.orderConfig = orderConfig;
        this.autoSendOrders = orderConfig.isAuto();
        this.mqService = mqService;
        this.kitchenOrder = kitchenOrder;

        // Load data
        String jsonStr = JSON.toJSONString(orderConfig.getData());
        orderList = JSON.parseArray(jsonStr, Order.class);
    }

    public int sendOrders() {
        if (!autoSendOrders) {
            return 0;
        }

        // No valid orders
        if (CollectionUtils.isEmpty(orderList)) {
            return 0;
        }

        // Send random orders
        int count = orderList.size();
        int batch = PoissonUtil.getVariable(orderConfig.getLambda());
        for (int i = 0; i < batch; i++) {
            index = (index + i) % count;
            Order order = orderList.get(index);

            // Send to message queue
            order.setId(CodeUtil.getCode());
            mqService.sendMessage(kitchenOrder, order);
        }
        return batch;
    }

    public boolean isAutoSendOrders() {
        return autoSendOrders;
    }

    public void setAutoSendOrders(boolean autoSendOrders) {
        this.autoSendOrders = autoSendOrders;
    }
}
