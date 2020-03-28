package com.starter.controller;

import com.starter.entity.Log;
import com.starter.service.impl.LogServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LogControllerTest {

    @Test
    public void testLog() {
        // Mock
        LogServiceImpl logService = Mockito.mock(LogServiceImpl.class);
        Log log = Mockito.mock(Log.class);

        // Stub
        Mockito.when(logService.save(Mockito.any())).thenReturn(true);
        Mockito.when(logService.getOne(Mockito.any())).thenReturn(log);
        Mockito.when(logService.count()).thenReturn(1);

        Mockito.when(log.getSummary()).thenReturn("test");

        // Call method
        LogController logController = new LogController(logService);
        HashMap<String, Object> ret = logController.log("ip", log.getSummary());

        // Verify
        Assertions.assertEquals(new HashMap<String, Object>() {{
            put("chk", "db");
            put("msg", "test");
            put("save", true);
            put("count", 1);
            put("status", true);
        }}, ret);

        Mockito.verify(logService, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(logService, Mockito.times(1)).getOne(Mockito.any());
        Mockito.verify(logService, Mockito.times(1)).count();
    }
}
