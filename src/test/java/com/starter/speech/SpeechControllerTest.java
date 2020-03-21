package com.starter.speech;

import com.common.enc.B64Util;
import com.common.file.FileUtil;
import com.common.http.RespEnum;
import com.common.util.JsonUtil;
import com.common.util.LogUtil;
import com.common.util.MacUtil;
import com.common.util.MapUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpeechControllerTest {
    @Autowired
    SpeechController speechController;

    @Test
    public void testTts() throws IOException {
        for (String text : new String[]{
                "百度AI语音合成测试",
//                "大家好，欢迎大家再次来到Jext技术社区。很荣幸为大家推荐今天的课程：SpringBoot网络开发进阶，REST接口开发和调用实战。",
//                "SpringBoot是Java开发Web系统时常用框架，有非常丰富的组件和易用的功能，REST API又是动静分离架构下主要的交互方式。",
//                "无论是开发提供API接口，还是通过HttpClient调用其他服务，都请您学习今天的课程吧！",
        }) {
            MockHttpServletResponse response = new MockHttpServletResponse();
            Object ret = speechController.tts(response, text, null, null, MacUtil.gtMacAddr());
            LogUtil.info(ret);
            Assertions.assertEquals(RespEnum.OK.getCode(), MapUtil.getInt((Map) ret, "code"));

            String fileName = MapUtil.getStr((Map) ret, "msg");
            MockMultipartFile multipart = new MockMultipartFile(
                    fileName, fileName, null,
                    response.getContentAsByteArray()
            );

            // asr with Multipart
            ret = testAsr(multipart);

            // asr with b64 passed in json
            long len = response.getContentLength();
            String b64Str = B64Util.encode(response.getContentAsByteArray());
            String format = FileUtil.getFileExt(fileName).replace(".", "");
            String body = JsonUtil.toStr(new HashMap<String, Object>() {{
                put("size", len);
                put("format", format);
                put("audio", b64Str);
            }});
            Assertions.assertEquals(ret, speechController.asr(null, body, MacUtil.gtMacAddr()));
        }
    }

    public Object testAsr(MultipartFile file) throws IOException {
        Object ret = speechController.asr(file, null, MacUtil.gtMacAddr());
        LogUtil.info(ret);
        Assertions.assertEquals(RespEnum.OK.getCode(), MapUtil.getInt((Map) ret, "code"));
        return ret;
    }

    @Test
    public void testChat() {
        Object ret = speechController.chat(null, "上海浦东张江", MacUtil.gtMacAddr());
        LogUtil.info(ret);
        Assertions.assertNotNull(ret);
    }

    @Test
    public void testWalle() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        Object ret = speechController.tts(response, "AI语音聊天接口", null, null, MacUtil.gtMacAddr());
        LogUtil.info(ret);
        Assertions.assertNotNull(ret);

        String fileName = MapUtil.getStr((Map) ret, "msg");
        MockMultipartFile multipart = new MockMultipartFile(
                fileName, fileName, null,
                response.getContentAsByteArray()
        );

        response = new MockHttpServletResponse();
        ret = speechController.walle(response, null, multipart, null, null, null, MacUtil.gtMacAddr());
        LogUtil.info(ret);
        Assertions.assertNotNull(ret);
    }
}
