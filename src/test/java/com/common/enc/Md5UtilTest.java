package com.common.enc;

import com.common.util.LogUtil;
import com.starter.StarterApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = StarterApplication.class)
public class Md5UtilTest {
    @Test
    public void testMd5() {
        String ret = Md5Util.md5("test");
        LogUtil.info(ret);
        Assertions.assertNotNull(ret);

        Assertions.assertNotNull(Md5Util.md5(""));
        Assertions.assertNull(Md5Util.md5((String) null));
    }
}
