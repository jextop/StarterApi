package com.common.http;

import com.common.util.JsonUtil;
import com.common.util.LogUtil;
import com.starter.StarterApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest(classes = StarterApplication.class)
public class IpUtilTest {
    @Test
    public void testGetLocalIP() {
        String ret = IpUtil.getLocalIP();
        LogUtil.info(ret);
        Assertions.assertNotNull(ret);

        LogUtil.info(IpUtil.getLocalUrl());
        LogUtil.info(IpUtil.getLocalUrl(8011));
    }

    @Test
    public void testGetLocalIPList() {
        List<String> ret = IpUtil.getLocalIPList();
        LogUtil.info(JsonUtil.toStr(ret));
        Assertions.assertNotNull(ret);
    }
}
