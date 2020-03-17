package com.common.enc;

import com.common.file.FileUtil;
import com.common.util.LogUtil;
import com.starter.StarterApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@SpringBootTest(classes = StarterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class Md5UtilTest {
    @Test
    public void testMd5Str() {
        String ret = Md5Util.md5("test");
        LogUtil.info(ret);
        Assertions.assertNotNull(ret);

        Assertions.assertNotNull(Md5Util.md5(""));
        Assertions.assertNull(Md5Util.md5((String) null));
    }

    @Test
    public void testMd5Data() {
        String ret = Md5Util.md5("test".getBytes());
        LogUtil.info(ret);
        Assertions.assertNotNull(ret);

        Assertions.assertNotNull(Md5Util.md5("".getBytes()));
        Assertions.assertNull(Md5Util.md5((byte[]) null));
    }

    @Test
    public void testMd5Stream() throws IOException {
        File file = File.createTempFile("tmp", ".txt");
        FileUtil.write(file.getPath(), "test".getBytes());
        FileInputStream fileStream = new FileInputStream(file);

        String ret = Md5Util.md5(fileStream);
        LogUtil.info(ret);
        Assertions.assertNotNull(ret);

        Assertions.assertNull(Md5Util.md5((InputStream) null));
    }
}
