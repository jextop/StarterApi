package com.starter.speech;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.common.enc.Md5Util;
import com.common.http.RespData;
import com.common.http.RespJsonObj;
import com.common.http.UrlUtil;
import com.common.util.LogUtil;
import com.starter.file.FileHelper;
import com.starter.file.FileTypeEnum;
import com.starter.http.HttpService;
import com.starter.cache.CacheService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class BaiduService {
    public static final String FILE_EXT = "wav";
    private static final String TOKEN_URL = "https://openapi.baidu.com/oauth/2.0/token";
    private static final String TTS_URL = "https://tsn.baidu.com/text2audio";
    private static final String ASR_URL = "http://vop.baidu.com/server_api";

    @Autowired
    HttpService httpService;

    @Autowired
    BaiduConfig baiduConfig;

    @Autowired
    CacheService cacheService;

    @Autowired
    FileHelper fileHelper;

    private String token;
    private Date expireDate;

    public String token() {
        if (StringUtils.isEmpty(token) || new Date().after(expireDate)) {
            synchronized (BaiduService.class) {
                if (StringUtils.isEmpty(token) || new Date().after(expireDate)) {
                    Map<String, String> headers = new HashMap<String, String>() {{
                        put("Content-Type", "application/x-www-form-urlencoded");
                    }};
                    Map<String, Object> params = new HashMap<String, Object>() {{
                        put("grant_type", "client_credentials"); // 固定为“client_credentials”
                        put("client_id", baiduConfig.clientId); // 应用的API Key
                        put("client_secret", baiduConfig.clientSecret); // 应用的Secret Key
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

    public Map<String, Object> ttsCached(String text, String uid) {
        // Find the saved file
        FileTypeEnum type = FileTypeEnum.Audio;
        String fileName = String.format("%s%s.%s", type.getFlag(), Md5Util.md5(text), FILE_EXT);
        String filePath = fileHelper.getFilePath(fileName);

        File file = new File(filePath, fileName);
        if (file.exists()) {
            return new HashMap<String, Object>() {{
                put("file", file);
                put("fileName", fileName);
            }};
        }

        // Call tts api
        RespData dataResp = tts(text, uid);
        byte[] dataBytes = dataResp.getBytes();

        // Save file to local storage
        try {
            fileHelper.save(dataBytes, fileName);
        } catch (IOException e) {
            LogUtil.error("Error when save tts data", e.getMessage());
        }

        return new HashMap<String, Object>() {{
            put("data", dataResp);
            put("fileName", fileName);
        }};
    }

    public RespData tts(String text, String uid) {
        Map<String, String> headers = new HashMap<String, String>() {{
            put("Content-Type", "application/x-www-form-urlencoded");
        }};
        Map<String, Object> params = new HashMap<String, Object>() {{
            put("tex", UrlUtil.encode(text)); // 合成文本，UTF-8编码，2048个中文字或者英文数字
            put("tok", token()); // 调用鉴权认证接口获取到的access_token
            put("cuid", StringUtils.isEmpty(uid) ? "starter_api" : uid); // 用户唯一标识，长度为60字符，常用MAC地址或IMEI码
            put("ctp", "1"); // 客户端类型选择，web端填写固定值1
            put("lan", "zh"); // 语言选择,目前只有中英文混合模式，固定值zh
            put("spd", "6"); // 语速，取值0-15，默认为5中语速
            put("pit", "5"); // 音调，取值0-15，默认为5中语调
            put("vol", "5"); // 音量，取值0-15，默认为5中音量
            put("per", "0"); // 0为普通女声，1为普通男生，3为情感合成-度逍遥，4为情感合成-度丫丫
            put("aue", "6"); // 3为mp3格式(默认)； 4为pcm-16k；5为pcm-8k；6为wav（内容同pcm-16k）
        }};

        RespData resp = new RespData();
        httpService.sendHttpForm(TTS_URL, headers, params, resp);
        return resp;
    }

    public JSONArray asr(String format, String b64Data, long len, String uid) {
        // Get md5 and check duplicated files
        String md5Str = Md5Util.md5(b64Data);
        String cacheKey = String.format("speech_asr_%s", md5Str);
        JSONArray cacheValue = (JSONArray) cacheService.get(cacheKey);
        if (CollectionUtils.isNotEmpty(cacheValue)) {
            return cacheValue;
        }

        Map<String, String> headers = new HashMap<String, String>() {{
            put("Content-Type", "application/json");
        }};

        Map<String, Object> params = new HashMap<String, Object>() {{
            put("format", format); // 音频格式：pcm/wav/amr/m4a，推荐pcm
            put("rate", 16000); // 音频采样频率，固定值16000
            put("dev_pid", 1537); // 语音模型，默认1537普通话，1737英语
            put("channel", 1); // 声道数量，仅支持单声道1
            put("cuid", StringUtils.isEmpty(uid) ? "starter_api" : uid); // 用户唯一标识，长度为60字符，常用MAC地址或IMEI码
            put("token", token()); // 调用鉴权认证接口获取到的access_token
            put("len", len); // 音频长度，base64前
            put("speech", b64Data); // 音频数据，base64（FILE_CONTENT）
        }};

        JSONObject ret = httpService.sendHttpPost(ASR_URL, headers, params, new RespJsonObj());
        cacheValue = ret.getJSONArray("result");
        cacheService.set1Month(cacheKey, cacheValue);
        return cacheValue;
    }
}
