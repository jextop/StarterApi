package com.common.enc;

import com.common.util.LogUtil;
import com.starter.StarterApplication;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = StarterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JwtUtilTest {
    @Test
    public void testGenerate() {
        for (String io : new String[]{"test", "admin", "", null}) {
            final String ret = JwtUtil.generate(io);
            LogUtil.info("String", io, "token", ret);

            Assertions.assertEquals(StringUtils.isEmpty(io) ? null : io, JwtUtil.parse(ret));
        }
    }
}
