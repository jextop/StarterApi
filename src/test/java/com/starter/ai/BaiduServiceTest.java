package com.starter.ai;


import com.common.http.RespData;
import com.common.util.LogUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BaiduServiceTest {
    @Autowired
    BaiduService baiduService;

    @Test
    public void testToken() {
        String ret = baiduService.token();
        LogUtil.info(ret);
        Assertions.assertNotNull(ret);
    }

    @Test
    public void testTts() {
        RespData respData = baiduService.tts("test tts测试语音合成接口");
        LogUtil.info(respData.getContentType(), respData.getContentLength());
        Assertions.assertNotNull(respData.getBytes());
    }
}

