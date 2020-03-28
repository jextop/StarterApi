package com.starter.kitchen.mock.order;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ding
 */
@Configuration
@ConfigurationProperties(prefix = "kitchen.mock.order")
public class MockOrderConfig {
    private String cron;
    private double lambda;
    private boolean auto;

    @Bean
    public JobDetail orderJob() {
        return JobBuilder.newJob(MockOrderJob.class)
                .storeDurably()
                .build();
    }

    @Bean
    public Trigger orderTrigger() {
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder
                .cronSchedule(cron)
                .withMisfireHandlingInstructionDoNothing();

        return TriggerBuilder.newTrigger()
                .forJob(orderJob())
                .withSchedule(scheduleBuilder)
                .build();
    }

    public boolean isAuto() {
        return auto;
    }

    public void setAuto(boolean auto) {
        this.auto = auto;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public double getLambda() {
        return lambda;
    }

    public void setLambda(double lambda) {
        this.lambda = lambda;
    }
}
