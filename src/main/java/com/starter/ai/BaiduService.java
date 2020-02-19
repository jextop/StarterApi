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

import java.util.HashMap;
import java.util.Map;

@Service
public class BaiduService {
    public static final String FILE_EXT = "wav";

    @Autowired
    HttpService httpService;

    String token;

    public String token() {
        if (StrUtil.isEmpty(token)) {
            synchronized (BaiduService.class) {
                if (StrUtil.isEmpty(token)) {
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
                    token = ret.getString("access_token");
                }
            }
        }
        return token;
    }

    public RespData tts(String text) {
        String url = "https://tsn.baidu.com/text2audio";
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
            put("per", "1");
            put("aue", "6"); // 3为mp3格式(默认)； 4为pcm-16k；5为pcm-8k；6为wav（内容同pcm-16k）
        }};

        RespData resp = new RespData();
        httpService.sendHttpForm(url, headers, params, resp);
        return resp;
    }
}
