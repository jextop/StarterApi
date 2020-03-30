package com.starter.track.mock;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.common.http.LocationUtil;
import com.common.util.LogUtil;
import com.common.util.MacUtil;
import com.common.util.PoissonUtil;
import com.starter.track.TrackController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author ding
 */
@Service
@Configuration
@Lazy
public class MockTrackClient {
    private volatile boolean autoSend;
    private TrackController trackController;

    @Autowired
    public MockTrackClient(TrackController trackController) throws IOException {
        this.trackController = trackController;
    }

    public int sendPosition() {
        if (!autoSend) {
            return 0;
        }

        // Send random ones
        int count = 30;
        int index = 0;
        do {
            int batch = PoissonUtil.getVariable(3);
            for (int i = 0; i < batch; i++) {
                index += i;
                if (index >= count) {
                    break;
                }

                String uid = String.format("%s%02d", MacUtil.gtMacAddr(), index);
                JSONObject location = LocationUtil.getLocation();
                trackController.track(uid, JSON.toJSONString(location));

                int j = (int) (Math.random() * 300);
                if (j > 0) {
                    try {
                        Thread.sleep(j);
                    } catch (InterruptedException e) {
                        LogUtil.error(e.getMessage());
                    }
                }
            }
        } while (index < count);
        return count;
    }

    public boolean isAutoSend() {
        return autoSend;
    }

    public void setAutoSend(boolean autoSend) {
        this.autoSend = autoSend;
    }
}
