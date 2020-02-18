package com.starter.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.common.util.LogUtil;
import com.starter.entity.Log;
import com.starter.service.impl.LogServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

@SpringBootTest
public class LogServiceTest {
    @Autowired
    LogServiceImpl logService;

    @Test
    public void testSave() {
        boolean ret = logService.save(new Log() {{
            setSummary(String.format("service: %s", new Date()));
        }});

        LogUtil.info(ret);
        Assertions.assertTrue(ret);

        testList();
    }

    public void testList() {
        List<Log> ret = logService.list(new QueryWrapper<Log>()
                .orderByDesc("id")
                .last(true, "limit 2")
        );

        ret.forEach(LogUtil::info);
        Assertions.assertFalse(ret.isEmpty());
    }
}
