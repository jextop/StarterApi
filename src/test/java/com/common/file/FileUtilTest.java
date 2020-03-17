package com.common.file;

import com.starter.StarterApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = StarterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class FileUtilTest {
    @Test
    public void testGetExt() {
        Map<String, String> mapIO = new HashMap<String, String>() {{
            put(null, null);
            put("a", "");
            put("a.b", ".b");
            put("a.jpg", ".jpg");
        }};
        for (Map.Entry<String, String> io : mapIO.entrySet()) {
            String ret = FileUtil.getFileExt(io.getKey());
            Assertions.assertEquals(io.getValue(), ret);
        }
    }

    @Test
    public void testFindSubFolders() {
        File[] ret = FileUtil.findSubFolders(".", true);
        System.out.printf("Folders: %d\n", ret == null ? 0 : ret.length);
        if (ret != null) {
            int index = 0;
            for (File file : ret) {
                System.out.println(file.getPath());
                if (index++ > 1) {
                    break;
                }
            }
        }
        Assertions.assertTrue(ret != null && ret.length > 0);
    }

    @Test
    public void testFindFiles() {
        File[] ret = FileUtil.findFiles(".", ".java", true);
        System.out.printf("Files: %d\n", ret == null ? 0 : ret.length);
        if (ret != null) {
            int count = 0;
            for (File file : ret) {
                System.out.println(file.getPath());
                if (count++ > 2) {
                    break;
                }
            }
        }
        Assertions.assertTrue(ret != null && ret.length > 0);
    }
}
