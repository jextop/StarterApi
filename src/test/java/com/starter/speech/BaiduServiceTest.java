package com.starter.speech;

import com.common.enc.B64Util;
import com.common.file.FileUtil;
import com.common.http.RespData;
import com.common.util.LogUtil;
import com.common.util.MacUtil;
import com.starter.file.FileHelper;
import com.starter.http.HttpService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.File;
import java.util.Map;

@SpringBootTest
public class BaiduServiceTest {
    @Autowired
    BaiduService baiduService;

    @Autowired
    HttpService httpService;

    @Autowired
    FileHelper fileHelper;

    @Test
    public void testToken() {
        String ret = baiduService.token();
        LogUtil.info(ret);
        Assertions.assertNotNull(ret);
    }

    @Test
    public void testTts() {
        RespData ret = baiduService.tts("test tts测试语音合成接口", MacUtil.gtMacAddr());
        LogUtil.info(ret.getContentType(), ret.getContentLength());
        Assertions.assertNotNull(ret.getBytes());

        long len = ret.getContentLength();
        String b64Str = B64Util.encode(ret.getBytes());
        String format = ret.getFileExt();

        testAsr(format, b64Str, len);
    }

    @Test
    public void testTtsCached() {
        Map<String, Object> ret = baiduService.ttsCached("test tts测试语音合成接口", MacUtil.gtMacAddr());
        LogUtil.info(ret);
        Assertions.assertTrue(ret.containsKey("file") || ret.containsKey("data"));

        if (ret.containsKey("data")) {
            RespData resp = (RespData) ret.get("data");

            long len = resp.getContentLength();
            String b64Str = B64Util.encode(resp.getBytes());
            String format = resp.getFileExt();

            testAsr(format, b64Str, len);
        } else {
            File file = (File) ret.get("file");
            MockHttpServletResponse resp = new MockHttpServletResponse();
            fileHelper.read(resp, file);

            long len = resp.getContentLength();
            String b64Str = B64Util.encode(resp.getContentAsByteArray());
            String format = FileUtil.getFileExt(file.getName()).replace(",", "");

            testAsr(format, b64Str, len);
        }
    }

    public void testAsr(String format, String b64Str, long len) {
        /*
        {
          "corpus_no": "6798700593866868782",
          "err_msg": "success.",
          "err_no": 0,
          "result": [
            "Ss tts测试语音合成接口。"
          ],
          "sn": "383242169911582945835"
        }
        */
        Object ret = baiduService.asr(format, b64Str, len, MacUtil.gtMacAddr());
        LogUtil.info(ret);
        Assertions.assertNotNull(ret);
    }
}
