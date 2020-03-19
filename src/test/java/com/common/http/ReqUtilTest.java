package com.common.http;

import com.common.util.LogUtil;
import com.starter.StarterApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

@SpringBootTest(classes = StarterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ReqUtilTest {
    @Test
    public void testGetIp() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        String ret = ReqUtil.getIp(request);
        LogUtil.info(ret);
        Assertions.assertNull(ret);

        request.setRemoteAddr(IpUtil.getLocalIP());
        ret = ReqUtil.getIp(request);
        LogUtil.info(ret);
        Assertions.assertNotNull(ret);
    }
}
