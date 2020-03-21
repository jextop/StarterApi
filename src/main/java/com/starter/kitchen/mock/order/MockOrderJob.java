package com.starter.kitchen.mock.order;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author ding
 */
public class MockOrderJob extends QuartzJobBean {
    private MockOrderSystem orderSystem;

    @Autowired
    public MockOrderJob(MockOrderSystem orderSystem) {
        this.orderSystem = orderSystem;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        // Send orders to kitchen
        orderSystem.sendOrders();
    }
}
