package com.starter.ai;

import com.common.http.RespEnum;
import com.common.util.LogUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

@SpringBootTest
public class AiControllerTest {
    @Autowired
    AiController aiController;

    @Test
    public void testTts() throws IOException {
        MockHttpServletResponse response = new MockHttpServletResponse();
        Object ret = aiController.tts(response, "测试Controller");
        LogUtil.info(ret);
        Assertions.assertEquals(RespEnum.OK.toMap(), ret);
    }
}
