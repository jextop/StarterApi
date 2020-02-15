package com.starter.file;

import com.common.file.FileUtil;
import com.common.http.RespEnum;
import com.common.util.LogUtil;
import com.starter.controller.FileController;
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
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
public class FileControllerTest {
    @Autowired
    FileController fileController;

    @Test
    public void testUpload() throws IOException {
        File emptyFile = File.createTempFile("tmp", ".txt");
        MockMultipartFile emptyMultipart = new MockMultipartFile(
                emptyFile.getName(), emptyFile.getName(), null,
                new FileInputStream(emptyFile)
        );

        File file = File.createTempFile("tmp", ".txt");
        FileUtil.write(file.getPath(), "upload tmp file".getBytes());
        MockMultipartFile multipart = new MockMultipartFile(
                file.getName(), file.getName(), null,
                new FileInputStream(file)
        );

        Map<MultipartFile, RespEnum> mapIO = new HashMap<MultipartFile, RespEnum>() {{
//            put(null, RespEnum.FILE_EMPTY);
//            put(emptyMultipart, RespEnum.FILE_EMPTY);
            put(multipart, RespEnum.OK);
        }};

        String url = null;
        for (Map.Entry<MultipartFile, RespEnum> io : mapIO.entrySet()) {
            Object ret = fileController.doUpload(FileTypeEnum.File, io.getKey(), null);
            LogUtil.info(ret);

            if (io.getValue().getCode() == RespEnum.OK.getCode()) {
                Map<String, Object> retMap = (Map<String, Object>) ret;
                Assertions.assertEquals(io.getValue().getCode(), retMap.get("code"));
                url = (String) retMap.get("url");
            } else {
                Assertions.assertEquals(io.getValue().toMap(), ret);
            }
        }

        // Test download
        testDownload(FileUtil.getFileName(url));
    }

    public void testDownload(String name) {
        Map<String, RespEnum> mapIO = new HashMap<String, RespEnum>() {{
//            put(String.format("%s.txt", CodeUtil.getCode()), RespEnum.NOT_FOUND);
            put(name, RespEnum.OK);
        }};

        HttpServletResponse response = new MockHttpServletResponse();
        for (Map.Entry<String, RespEnum> io : mapIO.entrySet()) {
            Object ret = fileController.doDownload(response, io.getKey());
            LogUtil.info(ret);
            Assertions.assertEquals(io.getValue().toMap(), ret);
        }
    }
}
