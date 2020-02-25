package com.starter.speech;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.http.RespJsonObj;
import com.common.util.EmptyUtil;
import com.common.util.MapUtil;
import com.starter.http.HttpService;
import com.starter.http.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TulingService {
    static final String API_URL = "http://openapi.tuling123.com/openapi/api/v2";

    @Autowired
    LocationService locationService;

    @Autowired
    HttpService httpService;

    @Autowired
    TulingConfig tulingConfig;

    public JSONArray chat(String text, String ip) {
        Map<String, String> headers = new HashMap<String, String>() {{
            put("Content-Type", "application/json");
        }};
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("reqType", 0);
            put("perception", new HashMap<String, Object>() {{
                put("inputText", new HashMap<String, Object>() {{
                    put("text", text);
                }});

                Map<String, Object> address = locationService.getAddress(ip);
                if (!EmptyUtil.isEmpty(address)) {
                    Map<String, Object> detail = MapUtil.getMap(address, "address_detail");
                    put("selfInfo", new HashMap<String, Object>() {{
                        put("location", new HashMap<String, Object>() {{
                            put("city", detail.get("city"));
                            put("province", detail.get("province"));
                            put("street", detail.get("street"));
                        }});
                    }});
                }
            }});
            put("userInfo", new HashMap<String, Object>() {{
                put("apiKey", tulingConfig.getApiKey());
                put("userId", "starter");
            }});
        }};

        /*
        {
          "intent": {
            "actionName": "",
            "code": 10037,
            "intentName": ""
          },
          "results": [
            {
              "groupType": 1,
              "resultType": "text",
              "values": {
                "text": "在外住酒店，还是得小心点好哦~"
              }
            }
          ]
        }
        */
        RespJsonObj resp = new RespJsonObj();
        JSONObject ret = httpService.sendHttpPost(API_URL, headers, params, resp);
        return ret.getJSONArray("results");
    }
}
