package com.starter.kitchen.mock.order;

import com.alibaba.fastjson.JSONArray;
import com.common.util.CodeUtil;
import com.common.util.PoissonUtil;
import com.common.util.ResUtil;
import com.starter.kitchen.Order;
import com.starter.kitchen.service.ActiveMqService;
import org.apache.commons.collections4.CollectionUtils;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
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

    ActiveMqService activeMqService;
    private Queue kitchenOrder;

    @Autowired
    public MockOrderSystem(
            MockOrderConfig orderConfig,
            ActiveMqService activeMqService,
            Queue kitchenOrder
    ) throws IOException {
        this.orderConfig = orderConfig;
        this.autoSendOrders = orderConfig.isAuto();
        this.activeMqService = activeMqService;
        this.kitchenOrder = kitchenOrder;

        // Load fake orders from resource file
        String jsonStr = ResUtil.readAsStr("kitchen_order.json");
        orderList = JSONArray.parseArray(jsonStr, Order.class);
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
            activeMqService.sendMessage(kitchenOrder, order);
        }
        return batch;
    }

    public boolean isAutoSendOrders() {
        return autoSendOrders;
    }

    public void setAutoSendOrders(boolean autoSendOrders) {
        this.autoSendOrders = autoSendOrders;
    }

    @Bean
    public JobDetail orderJob() {
        return JobBuilder.newJob(MockOrderJob.class)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger orderTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
                .cronSchedule(orderConfig.getCron())
                .withMisfireHandlingInstructionDoNothing();

        return TriggerBuilder.newTrigger()
                .forJob(orderJob())
                .withSchedule(scheduleBuilder)
                .build();
    }
}
