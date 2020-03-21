package com.starter.kitchen;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * Timer job to update shelf
 *
 * @author ding
 */
public class KitchenJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            SchedulerContext schedulerContext = context.getScheduler().getContext();

            // Scheduled job to remove order waste and update admin page
            KitchenService kitchenService = (KitchenService) schedulerContext.get(KitchenService.class.getSimpleName());
            kitchenService.updateShelf();
            kitchenService.notifyAdmin();

            // Re-schedule job for new minimum zero-value time
            kitchenService.scheduleJob();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }
}
