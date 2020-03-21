package com.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @author ding
 */
public class JsonUtil {
    public static String toStr(Object javaObj) {
        return javaObj == null ? null : JSON.toJSONString(javaObj);
    }

    public static JSONObject parseObj(String jsonStr) {
        if (StringUtils.isNotBlank(jsonStr)) {
            try {
                return JSON.parseObject(jsonStr);
            } catch (JSONException e) {
                LogUtil.warn("Exception when parseObj", e.getMessage());
            }
        }
        return null;
    }

    public static <T> T parseObj(String jsonStr, Class<T> clazz) {
        if (StringUtils.isNotBlank(jsonStr)) {
            try {
                return JSON.parseObject(jsonStr, clazz);
            } catch (JSONException e) {
                LogUtil.warn("Exception when parseObj", e.getMessage());
            }
        }
        return null;
    }

    public static JSONArray parseArr(String jsonStr) {
        if (StringUtils.isNotBlank(jsonStr)) {
            try {
                return JSON.parseArray(jsonStr);
            } catch (Exception e) {
                LogUtil.warn("Exception when parseArr", e.getMessage());
            }
        }
        return null;
    }

    public static <T> List<T> parseArr(String jsonStr, Class<T> clazz) {
        if (StringUtils.isNotBlank(jsonStr)) {
            try {
                return JSON.parseArray(jsonStr, clazz);
            } catch (Exception e) {
                LogUtil.warn("Exception when parseArr", e.getMessage());
            }
        }
        return null;
    }
}
