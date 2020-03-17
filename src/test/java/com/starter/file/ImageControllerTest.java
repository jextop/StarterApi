package com.starter.file;

import com.common.file.FileUtil;
import com.common.http.RespEnum;
import com.common.util.CodeUtil;
import com.common.util.EmptyUtil;
import com.common.util.JsonUtil;
import com.common.util.LogUtil;
import com.starter.StarterApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = StarterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ImageControllerTest {
    @Autowired
    ImageController imageController;

    @Test
    public void testUpload() throws IOException {
        File txtFile = File.createTempFile("tmp", ".txt");
        FileUtil.write(txtFile.getPath(), "tmp text file".getBytes());
        MockMultipartFile txtMultipart = new MockMultipartFile(
                txtFile.getName(), txtFile.getName(), null,
                new FileInputStream(txtFile)
        );

        File emptyFile = File.createTempFile("tmp", ".png");
        MockMultipartFile emptyMultipart = new MockMultipartFile(
                emptyFile.getName(), emptyFile.getName(), null,
                new FileInputStream(emptyFile)
        );

        File pngFile = File.createTempFile("tmp", ".png");
        FileUtil.write(pngFile.getPath(), "test png file".getBytes());
        MockMultipartFile pngMultipart = new MockMultipartFile(
                pngFile.getName(), pngFile.getName(), null,
                new FileInputStream(pngFile)
        );

        Map<MultipartFile, RespEnum> mapIO = new HashMap<MultipartFile, RespEnum>() {{
            put(txtMultipart, RespEnum.UNSUPPORTED_MEDIA_TYPE);
            put(emptyMultipart, RespEnum.FILE_EMPTY);
            put(pngMultipart, RespEnum.OK);
        }};

        String url = null;
        for (Map.Entry<MultipartFile, RespEnum> io : mapIO.entrySet()) {
            Object ret = imageController.upload(io.getKey(), "1");
            LogUtil.info(ret);

            Map<String, Object> retMap = (Map<String, Object>) ret;
            Assertions.assertEquals(io.getValue().getCode(), retMap.get("code"));
            if (io.getValue().getCode() == RespEnum.OK.getCode()) {
                url = (String) retMap.get("url");
            }
        }

        testDownload(FileUtil.getFileName(url));
        testList();
    }

    public void testDownload(final String name) {
        Map<String, RespEnum> mapIO = new HashMap<String, RespEnum>() {{
            put(String.format("%s.png", CodeUtil.getCode()), RespEnum.NOT_FOUND);
            put(name, RespEnum.OK);
        }};

        final HttpServletResponse response = new MockHttpServletResponse();
        for (Map.Entry<String, RespEnum> io : mapIO.entrySet()) {
            Object ret = imageController.download(response, io.getKey());
            LogUtil.info(ret);
            Assertions.assertEquals(io.getValue().toMap(), ret);
        }
    }

    public void testList() {
        Object ret = imageController.list("");
        ret = ((Map<String, ?>) ret).get("items");
        LogUtil.info(JsonUtil.toStr(ret));
        Assertions.assertFalse(EmptyUtil.isEmpty((Collection) ret));
    }
}
