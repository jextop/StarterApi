package com.common.http;

import com.common.util.LogUtil;
import com.starter.StarterApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = StarterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UrlUtilTest {
    @Test
    public void testEncode() {
        String str = "test enc, 测试编码，";
        String ret = UrlUtil.encode(str);
        LogUtil.info(ret, str);
        Assertions.assertEquals(UrlUtil.decode(ret), str);
    }
}
