package com.starter.http;

import com.alibaba.fastjson.JSONObject;
import com.common.http.RespData;
import com.common.http.RespJsonObj;
import com.common.http.UrlUtil;
import com.common.util.LogUtil;
import com.common.util.StrUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class HttpServiceTest {
    @Autowired
    HttpService httpService;

    @Test
    public void testSendHttpGet() {
        String html = httpService.sendHttpGet("https://blog.51cto.com/13851865");
        String[] ret = StrUtil.parse(html, "<span>[1-9]\\d*</span>");
        LogUtil.info(ret);
        Assertions.assertNotNull(ret);
    }

    @Test
    public void testSendHttpForm() {
        String url = "https://openapi.baidu.com/oauth/2.0/token";
        Map<String, String> headers = new HashMap<String, String>() {{
            put("Content-Type", "application/x-www-form-urlencoded");
        }};
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("grant_type", "client_credentials");
            put("client_id", "kVcnfD9iW2XVZSMaLMrtLYIz");
            put("client_secret", "O9o1O213UgG5LFn0bDGNtoRN3VWl2du6");
        }};

        JSONObject ret = httpService.sendHttpForm(url, headers, params, new RespJsonObj());
        LogUtil.info(ret);
        Assertions.assertNotNull(ret);

        String token = ret.getString("access_token");
        LogUtil.info(token);
        testBaiduTts(token);
    }

    public void testBaiduTts(String token) {
        String url = "https://tsn.baidu.com/text2audio";
        Map<String, String> headers = new HashMap<String, String>() {{
            put("Content-Type", "application/x-www-form-urlencoded");
        }};
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("tex", UrlUtil.encode("SpringBoot搭建分布式Web服务脚手架"));
            put("tok", token);
            put("cuid", "starter_api_http_service");
            put("ctp", "1");
            put("lan", "zh");
            put("spd", "6");
            put("per", "1");
        }};

        RespData resp = new RespData();
        byte[] ret = httpService.sendHttpForm(url, headers, params, resp);
        Assertions.assertNotNull(ret);

        String file = resp.saveFile("http_service_test.mp3");
        LogUtil.info(file);
    }
}
