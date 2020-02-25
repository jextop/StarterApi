package com.starter.ai;

import com.alibaba.fastjson.JSONObject;
import com.common.http.RespData;
import com.common.http.RespJsonObj;
import com.common.http.UrlUtil;
import com.common.util.LogUtil;
import com.common.util.StrUtil;
import com.starter.http.HttpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class BaiduService {
    public static final String FILE_EXT = "wav";
    static final String TOKEN_URL = "https://openapi.baidu.com/oauth/2.0/token";
    static final String TTS_URL = "https://tsn.baidu.com/text2audio";

    @Autowired
    HttpService httpService;

    @Autowired
    BaiduConfig baiduConfig;

    String token;
    Date expireDate;

    public String token() {
        if (StrUtil.isEmpty(token) || new Date().after(expireDate)) {
            synchronized (BaiduService.class) {
                if (StrUtil.isEmpty(token) || new Date().after(expireDate)) {
                    Map<String, String> headers = new HashMap<String, String>() {{
                        put("Content-Type", "application/x-www-form-urlencoded");
                    }};
                    Map<String, Object> params = new HashMap<String, Object>() {{
                        put("grant_type", "client_credentials");
                        put("client_id", baiduConfig.clientId);
                        put("client_secret", baiduConfig.clientSecret);
                    }};

                    JSONObject ret = httpService.sendHttpForm(TOKEN_URL, headers, params, new RespJsonObj());
                    LogUtil.info("Baidu AI token", ret);
                    token = ret.getString("access_token");

                    long seconds = ret.getLongValue("expires_in");
                    expireDate = new Date(System.currentTimeMillis() + seconds * 1000);
                }
            }
        }
        return token;
    }

    public RespData tts(String text) {
        Map<String, String> headers = new HashMap<String, String>() {{
            put("Content-Type", "application/x-www-form-urlencoded");
        }};
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("tex", UrlUtil.encode(text));
            put("tok", token());
            put("cuid", "starter_api_http_service");
            put("ctp", "1");
            put("lan", "zh");
            put("spd", "6");
            put("pit", "5");
            put("vol", "5");
            put("per", "0");
            put("aue", "6"); // 3为mp3格式(默认)； 4为pcm-16k；5为pcm-8k；6为wav（内容同pcm-16k）
        }};

        RespData resp = new RespData();
        httpService.sendHttpForm(TTS_URL, headers, params, resp);
        return resp;
    }
}
