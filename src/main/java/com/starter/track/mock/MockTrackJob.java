package com.starter.track.mock;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

/**
 * @author ding
 */
public class MockTrackJob extends QuartzJobBean {
    private MockTrackClient trackClient;

    @Autowired
    public MockTrackJob(MockTrackClient trackClient) {
        this.trackClient = trackClient;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        trackClient.sendPosition();
    }
}
