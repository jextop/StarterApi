package com.common.http;

import com.alibaba.fastjson.JSONObject;
import com.common.util.LogUtil;
import com.starter.StarterApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = StarterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LocationUtilTest {
    @Test
    public void testGetLocation() {
        /*
        {
            "regionCode": "0",
            "regionNames": "",
            "proCode": "310000",
            "err": "",
            "city": "上海市",
            "cityCode": "310000",
            "ip": "101.229.196.154",
            "pro": "上海市",
            "region": "",
            "addr": "上海市 电信"
        }
         */
        JSONObject ret = LocationUtil.getLocation();
        LogUtil.info(ret);
        Assertions.assertNotNull(ret);
    }
}
