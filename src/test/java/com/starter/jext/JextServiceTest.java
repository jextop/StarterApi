package com.starter.jext;

import com.common.util.JsonUtil;
import com.common.util.LogUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JextServiceTest {
    @Autowired
    JextService jextService;

    @Test
    public void testGetInfo() {
        Map<String, Object> ret = jextService.getInfo(true);
        LogUtil.info(JsonUtil.toStr(ret));
        Assertions.assertFalse(ret.isEmpty());
    }
}
