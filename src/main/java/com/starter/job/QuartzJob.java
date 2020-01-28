package com.starter.job;

import com.common.util.LogUtil;
import com.starter.jext.JextService;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.SchedulerException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;

public class QuartzJob extends QuartzJobBean {
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        LogUtil.info("quartz job ", new Date());

        try {
            JextService jextService = (JextService) context.getScheduler().getContext().get("jextService");
            if (jextService != null) {
                jextService.getInfo(true);
            }
        } catch (SchedulerException e) {
            LogUtil.info(e.getMessage());
        }
    }
}
