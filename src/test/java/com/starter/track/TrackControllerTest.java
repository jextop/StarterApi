package com.starter.track;

import com.common.util.LogUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
