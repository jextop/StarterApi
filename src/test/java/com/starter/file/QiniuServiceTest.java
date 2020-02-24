package com.starter.file;

import com.common.file.FileUtil;
import com.common.util.EmptyUtil;
import com.common.util.LogUtil;
import com.common.util.StrUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@ConditionalOnBean(QiniuService.class)
public class QiniuServiceTest {
    @Autowired
    QiniuService qiniuService;

    @Test
    public void testUpload() throws IOException {
        File emptyFile = File.createTempFile("tmp", ".txt");

        File file = File.createTempFile("tmp", ".txt");
        FileUtil.write(file.getPath(), "upload temp file".getBytes());

        Map<File, Boolean> mapIO = new HashMap<File, Boolean>() {{
            put(null, false);
//            put(emptyFile, true);
            put(file, true);
        }};

        for (Map.Entry<File, Boolean> io : mapIO.entrySet()) {
            File f = io.getKey();
            String ret = qiniuService.uploadFile(f == null ? null : f.getPath(), null);
            LogUtil.info("uploadFile", ret);
            Assertions.assertEquals(io.getValue(), !StrUtil.isEmpty(ret));

            // file path
            ret = qiniuService.upload(f == null ? null : f.getPath(), null);
            LogUtil.info("upload file path", ret);
            Assertions.assertEquals(io.getValue(), !StrUtil.isEmpty(ret));

            // file
            ret = qiniuService.upload(f, null);
            LogUtil.info("upload file", ret);
            Assertions.assertEquals(io.getValue(), !StrUtil.isEmpty(ret));

            // file
            ret = qiniuService.upload(f == null ? null : new FileInputStream(f), null);
            LogUtil.info("upload file stream", ret);
            Assertions.assertEquals(io.getValue(), !StrUtil.isEmpty(ret));

            // file data. Empty file is not uploaded.
            ret = qiniuService.upload(f == null ? null : readData(f), null);
            LogUtil.info("upload file data", ret);
            Assertions.assertEquals(io.getValue(), !StrUtil.isEmpty(ret));
        }

        testList();
    }

    public void testList() {
        Collection ret = qiniuService.list();
        LogUtil.info(ret.size());
        Assertions.assertFalse(EmptyUtil.isEmpty(ret));
    }

    public byte[] readData(File file) throws IOException {
        FileInputStream fileStream = new FileInputStream(file);
        BufferedInputStream bufferStream = new BufferedInputStream(fileStream);

        byte[] data = new byte[1024 * 100];
        int i = bufferStream.read(data);
        if (i <= 0) {
            data = null;
        }

        bufferStream.close();
        fileStream.close();
        return data;
    }
}
