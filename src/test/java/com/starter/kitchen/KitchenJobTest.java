package com.starter.kitchen;

import org.junit.jupiter.api.Test;
import org.quartz.JobExecutionContext;
import org.quartz.Scheduler;
import org.quartz.SchedulerContext;
import org.quartz.SchedulerException;
import org.springframework.boot.test.context.SpringBootTest;

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
public class KitchenJobTest {
    @Test
    public void testExecuteInternal() throws SchedulerException {
        // Mock kitchen service
        KitchenService kitchenService = mock(KitchenService.class);
        doNothing().when(kitchenService).updateShelf();
        doNothing().when(kitchenService).notifyAdmin();
        doNothing().when(kitchenService).scheduleJob();

        // Mock context
        SchedulerContext schedulerContext = mock(SchedulerContext.class);
        when(schedulerContext.get(anyString())).thenReturn(kitchenService);

        Scheduler scheduler = mock(Scheduler.class);
        when(scheduler.getContext()).thenReturn(schedulerContext);

        JobExecutionContext jobContext = mock(JobExecutionContext.class);
        when(jobContext.getScheduler()).thenReturn(scheduler);

        // Verify
        KitchenJob job = new KitchenJob();
        job.executeInternal(jobContext);
        verify(kitchenService, times(1)).updateShelf();
        verify(kitchenService, times(1)).notifyAdmin();
        verify(kitchenService, times(1)).scheduleJob();

        // Exception
        doThrow(new SchedulerException("unit testing mock")).when(scheduler).getContext();
        job.executeInternal(jobContext);
        verify(kitchenService, times(1)).updateShelf();
        verify(kitchenService, times(1)).notifyAdmin();
        verify(kitchenService, times(1)).scheduleJob();
    }
}
