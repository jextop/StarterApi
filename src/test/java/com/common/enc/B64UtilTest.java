package com.common.enc;

import com.common.util.LogUtil;
import com.starter.StarterApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = StarterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class B64UtilTest {
    @Test
    public void testEncode() {
        String str = "{\"aue\":\"raw\",\"auf\":\"audio/L16;rate=16000\",\"voice_name\":\"xiaoyan\",\"engine_type\":\"intp65\"}";
        String enc = B64Util.encode(str);
        String ret = B64Util.decode(enc);
        LogUtil.info(ret, enc);

        Assertions.assertEquals(str, ret);
    }
}
