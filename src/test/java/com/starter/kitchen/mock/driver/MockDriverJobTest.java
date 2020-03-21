package com.starter.kitchen.mock.driver;

import com.starter.kitchen.KitchenJob;
import com.starter.kitchen.KitchenService;
import org.junit.jupiter.api.Test;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockDriverJobTest {
    @Test
    public void testExecuteInternal() throws SchedulerException {
        // Mock driver system
        MockDriverSystem driverSystem = mock(MockDriverSystem.class);
        doNothing().when(driverSystem).delivery();
        doNothing().when(driverSystem).scheduleJob();

        // Mock context
        SchedulerContext schedulerContext = mock(SchedulerContext.class);
        when(schedulerContext.get("driver")).thenReturn(driverSystem);

        Scheduler scheduler = mock(Scheduler.class);
        when(scheduler.getContext()).thenReturn(schedulerContext);

        JobExecutionContext jobContext = mock(JobExecutionContext.class);
        when(jobContext.getScheduler()).thenReturn(scheduler);

        // Verify
        MockDriverJob job = new MockDriverJob();
        job.executeInternal(jobContext);
        verify(driverSystem, times(1)).delivery();
        verify(driverSystem, times(1)).scheduleJob();

        // Exception
        doThrow(new SchedulerException("unit testing mock")).when(scheduler).getContext();
        job.executeInternal(jobContext);
        verify(driverSystem, times(1)).delivery();
        verify(driverSystem, times(1)).scheduleJob();
    }
}
