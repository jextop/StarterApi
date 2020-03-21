package com.starter.kitchen.mock.driver;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

/**
 * @author ding
 */
@Component
public class MockDriverJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            SchedulerContext schedulerContext = context.getScheduler().getContext();

            // Scheduled job, pickup and delivery
            MockDriverSystem driverSystem = (MockDriverSystem) schedulerContext.get("driver");
            driverSystem.delivery();

            // Re-schedule job for new minimum zero-value time
            driverSystem.scheduleJob();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
