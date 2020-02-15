package com.starter.file;

import com.common.util.LogUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;


@SpringBootTest
public class FileHelperTest {
    @Autowired
    FileHelper fileHelper;

    @Test
    public void testGetFilePath() {
        Map<String, String> mapIo = new HashMap<String, String>() {{
            put("3", FileTypeEnum.File.getName());
            put(FileTypeEnum.Image.getFlag(), FileTypeEnum.Image.getName());
        }};

        for (Map.Entry<String, String> io : mapIo.entrySet()) {
            String ret = fileHelper.getFilePath(io.getKey());
            LogUtil.info(ret);
            if (ret == null) {
                Assertions.assertNull(io.getValue());
            } else {
                Assertions.assertTrue(ret.endsWith(io.getValue()));
            }
        }
    }
}
