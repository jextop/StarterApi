package com.starter.speech;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.enc.Md5Util;
import com.common.http.RespJsonObj;
import com.common.util.MapUtil;
import com.common.util.StrUtil;
import com.starter.http.HttpService;
import com.starter.http.LocationService;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TulingService {
    private static final String API_URL = "http://openapi.tuling123.com/openapi/api/v2";

    @Autowired
    LocationService locationService;

    @Autowired
    HttpService httpService;

    @Autowired
    TulingConfig tulingConfig;

    public JSONArray chat(String text, String ip, String uid) {
        Map<String, String> headers = new HashMap<String, String>() {{
            put("Content-Type", "application/json");
        }};
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("reqType", 0); // 输入类型，目前只支持文本0
            put("perception", new HashMap<String, Object>() {{
                put("inputText", new HashMap<String, Object>() {{
                    put("text", text); // 请求文本信息
                }});

                Map<String, Object> address = locationService.getAddress(ip);
                if (MapUtils.isNotEmpty(address)) {
                    Map<String, Object> detail = MapUtil.getMap(address, "address_detail");
                    put("selfInfo", new HashMap<String, Object>() {{
                        put("location", new HashMap<String, Object>() {{
                            put("city", detail.get("city")); // 所在城市
                            put("province", detail.get("province")); // 省市
                            put("street", detail.get("street")); // 街道
                        }});
                    }});
                }
            }});
            put("userInfo", new HashMap<String, Object>() {{
                put("apiKey", tulingConfig.getApiKey()); // 机器人标识
                put("userId", StringUtils.isEmpty(uid) ? "starter" : Md5Util.md5(uid)); // 用户唯一标识
            }});
        }};

        RespJsonObj resp = new RespJsonObj();
        JSONObject ret = httpService.sendHttpPost(API_URL, headers, params, resp);
        return ret.getJSONArray("results");
    }
}
