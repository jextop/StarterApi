package com.starter.http;

import com.alibaba.fastjson.JSONObject;
import com.common.enc.Md5Util;
import com.common.http.RespJsonObj;
import com.common.util.EmptyUtil;
import com.common.util.StrUtil;
import com.starter.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

@Service
public class LocationService {
    private static final String API_NAME = "/location/ip";
    private static final String API_URL = "https://api.map.baidu.com/location/ip";

    @Autowired(required = false)
    LocationConfig locationConfig;

    @Autowired
    HttpService httpService;

    @Autowired
    RedisService redisService;

    public Map<String, Object> getAddress(String ip) {
        if (StrUtil.isEmpty(ip)) {
            return null;
        }

        String cacheKey = String.format("ip_address_%s", ip);
        Map<String, Object> cacheValue = (Map<String, Object>) redisService.get(cacheKey);
        if (cacheValue != null || locationConfig == null) {
            return cacheValue;
        }

        // 计算sn跟参数对出现顺序有关，get请求请使用LinkedHashMap保存<key,value>，该方法根据key的插入顺序排序；
        // post请使用TreeMap保存<key,value>，该方法会自动将key按照字母a-z顺序排序。
        // 所以get请求可自定义参数顺序（sn参数必须在最后）发送请求，
        // 但是post请求必须按照字母a-z顺序填充body（sn参数必须在最后）。
        // 以get请求为例：https://api.map.baidu.com/location/ip?ip=yourip&ak=yourak&sn=yoursn
        // paramsMap中先放入ip，然后放ak，放入顺序必须跟get请求中对应参数的出现顺序保持一致。
        Map paramsMap = new LinkedHashMap<String, String>();
        paramsMap.put("ip", ip);
        paramsMap.put("ak", locationConfig.getAk());

        try {
            // 调用下面的toQueryString方法，对LinkedHashMap内所有value作utf8编码，拼接返回结果ip=yourip&ak=yourak
            String paramsStr = toQueryString(paramsMap);

            // 对paramsStr前面拼接上/location/ip?，后面直接拼接yoursk得到/location/ip?ip=yourip&ak=yourakyoursk
            String wholeStr = String.format("%s?%s%s", API_NAME, paramsStr, locationConfig.getSk());

            // 对上面wholeStr再作utf8编码
            String tempStr = URLEncoder.encode(wholeStr, "UTF-8");

            // 调用下面的MD5方法得到最后的sn签名7de5a22212ffaa9e326444c75a58f9a0
            String sn = Md5Util.md5(tempStr);
            paramsStr = String.format("%s?%s&sn=%s", API_URL, paramsStr, sn);
            JSONObject ret = httpService.sendHttpGet(paramsStr, new RespJsonObj());

            cacheValue = ret.getJSONObject("content");
            redisService.set1Month(cacheKey, cacheValue);
            return cacheValue;
        } catch (UnsupportedEncodingException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    /**
     * 对Map内所有value作utf8编码，拼接返回结果
     */
    public String toQueryString(Map<?, ?> data) throws UnsupportedEncodingException {
        StringBuffer queryString = new StringBuffer();
        for (Entry<?, ?> pair : data.entrySet()) {
            queryString.append(pair.getKey() + "=");
            queryString.append(URLEncoder.encode((String) pair.getValue(), "UTF-8") + "&");
        }
        if (queryString.length() > 0) {
            queryString.deleteCharAt(queryString.length() - 1);
        }
        return queryString.toString();
    }
}
