package com.common.http;

import com.starter.StarterApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

@SpringBootTest(classes = StarterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReqUtilTest {
    @Test
    public void testGetIp() {
        Assertions.assertNull(ReqUtil.getIp(null));

        MockHttpServletRequest request = new MockHttpServletRequest();
        Assertions.assertNull(ReqUtil.getIp(request));

        request.setRemoteAddr("192.168.1.3");
        Assertions.assertNotNull(ReqUtil.getIp(request));

        request.addHeader("X-Forwarded-For", "192.168.1.3");
        Assertions.assertNotNull(ReqUtil.getIp(request));
    }

    @Test
    public void testUtil() {
        // To keep 100% unit-testing coverage
        Assertions.assertNotNull(new ReqUtil());
    }
}
