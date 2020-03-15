package com.starter.track;

import com.common.enc.B64Util;
import com.common.file.FileUtil;
import com.common.http.RespEnum;
import com.common.util.JsonUtil;
import com.common.util.LogUtil;
import com.common.util.MacUtil;
import com.common.util.MapUtil;
import com.starter.speech.SpeechController;
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

@SpringBootTest
public class TrackControllerTest {
    @Autowired
    TrackController trackController;

    @Test
    public void testTrack() throws IOException {
        Object ret = trackController.track(null, null);
        LogUtil.info(ret);
        Assertions.assertNotNull(ret);
    }
}
