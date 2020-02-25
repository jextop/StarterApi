package com.starter.speech;

import com.common.enc.B64Util;
import com.common.file.FileUtil;
import com.common.http.RespData;
import com.common.util.LogUtil;
import com.starter.http.HttpService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BaiduServiceTest {
    @Autowired
    BaiduService baiduService;

    @Autowired
    HttpService httpService;

    @Test
    public void testToken() {
        String ret = baiduService.token();
        LogUtil.info(ret);
        Assertions.assertNotNull(ret);
    }

    @Test
    public void testTts() {
        RespData resp = baiduService.tts("test tts测试语音合成接口");
        LogUtil.info(resp.getContentType(), resp.getContentLength());
        Assertions.assertNotNull(resp.getBytes());

        long len = resp.getContentLength();
        String b64Str = B64Util.encode(resp.getBytes());
        String format = resp.getFileExt();

        Object ret = baiduService.asr(format, b64Str, len);
        LogUtil.info(ret);
        Assertions.assertNotNull(ret);
    }

    @Test
    public void testAsr() {
        String filePath = "http://q671m4cqj.bkt.clouddn.com/16k.pcm";
        RespData resp = new RespData();
        String b64Str = B64Util.encode(httpService.sendHttpGet(filePath, resp));
        long len = resp.getContentLength();
        String format = FileUtil.getFileExt(filePath).replace(".", "");

        Object ret = baiduService.asr(format, b64Str, len);
        LogUtil.info(ret);
        Assertions.assertNotNull(ret);
    }
}
