package com.starter.ai;

import com.common.util.LogUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TulingServiceTest {
    @Autowired
    TulingService tulingService;

    @Test
    public void testChat() {
        Object ret = tulingService.chat("你知道上海的天气吗？", null);
        LogUtil.info(ret);
        Assertions.assertNotNull(ret);
    }
}
