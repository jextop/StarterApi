package com.common.http;

import com.alibaba.fastjson.JSONObject;
import com.common.util.JsonUtil;
import com.common.util.StrUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public class LocationUtil {
    private static JSONObject location = null;

    public static JSONObject getLocation() {
        if (location == null) {
            synchronized (LocationUtil.class) {
                if (location == null) {
                    location = getLoc();
                }
            }
        }
        return location;
    }

    private static JSONObject getLoc() {
        String address = HttpUtil.sendHttpGet("http://whois.pconline.com.cn/ipJson.jsp");
        if (StringUtils.isBlank(address)) {
            return null;
        }

        String[] jsonStrArr = StrUtil.parse(address, "\\{\"\\S*\\s*\\S*\"\\}");
        return ArrayUtils.isEmpty(jsonStrArr) ? null : JsonUtil.parseObj(jsonStrArr[0]);
    }
}
