package com.starter.kitchen.mock.order;

import org.junit.jupiter.api.Test;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerException;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MockOrderJobTest {
    @Test
    public void testExecuteInternal() throws SchedulerException {
        // Mock order system
        MockOrderSystem orderSystem = mock(MockOrderSystem.class);
        when(orderSystem.sendOrders()).thenReturn(0);

        MockOrderJob job = new MockOrderJob(orderSystem);

        // Verify
        job.executeInternal(mock(JobExecutionContext.class));
        verify(orderSystem, times(1)).sendOrders();
    }
}
