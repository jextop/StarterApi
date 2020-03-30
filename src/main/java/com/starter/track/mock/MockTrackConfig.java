package com.starter.track.mock;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ding
 */
@Configuration
public class MockTrackConfig {
    @Bean
    public JobDetail trackJob() {
        return JobBuilder.newJob(MockTrackJob.class)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger trackTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
                .cronSchedule("0/7 * * * * ?")
                .withMisfireHandlingInstructionDoNothing();

        return TriggerBuilder.newTrigger()
                .forJob(trackJob())
                .withSchedule(scheduleBuilder)
                .build();
    }
}
