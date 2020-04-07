package com.starter.speech;

import com.alibaba.fastjson.JSONArray;
import com.common.enc.B64Util;
import com.common.file.FileUtil;
import com.common.http.ParamUtil;
import com.common.http.RespData;
import com.common.http.RespEnum;
import com.common.http.RespUtil;
import com.common.util.LogUtil;
import com.common.util.MapUtil;
import com.common.util.StrUtil;
import com.starter.annotation.AccessLimited;
import com.starter.file.FileHelper;
import com.starter.file.LocationEnum;
import com.starter.file.QiniuConfig;
import com.starter.file.QiniuService;
import com.starter.track.TrackConsumer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@Api(tags = {"AI语音聊天"})
@RestController
@RequestMapping("/speech")
public class SpeechController {
    public static final String[] fileExtArr = new String[]{".wav", ".pcm", ".amr", ".m4a"};

    @Autowired
    BaiduService baiduService;

    @Autowired
    FileHelper fileHelper;

    @Autowired(required = false)
    QiniuService qiniuService;

    @Autowired(required = false)
    QiniuConfig qiniuConfig;

    @Autowired
    TulingService tulingService;

    @AccessLimited(count = 1)
    @ApiOperation("语音合成")
    @GetMapping("/tts")
    public Object tts(
            HttpServletResponse response,
            @RequestParam("text") String text,
            @RequestParam(value = "url", required = false) String hasUrl,
            @RequestParam(value = "data", required = false) String hasData,
            @RequestParam(required = false) String uid
    ) {
        LogUtil.info("/speech/tts", text, hasUrl, hasData);
        boolean retUrl = StringUtils.isNotEmpty(hasUrl) && "1".equals(hasUrl);
        boolean retData = !retUrl || StringUtils.isEmpty(hasData) || !"0".equals(hasData);

        Map<String, Object> ttsMap = baiduService.ttsCached(text, uid);
        String fileName = MapUtil.getStr(ttsMap, "fileName");
        if (ttsMap.containsKey("file")) {
            File file = (File) ttsMap.get("file");

            if (retData) {
                // Read file
                fileHelper.read(response, file);
            }

            if (retUrl) {
                if (qiniuConfig != null) {
                    fileName = qiniuConfig.getFileUrl(fileName);
                } else {
                    fileName = fileHelper.getFileUrl(LocationEnum.Service, fileName);
                }
            }
            return RespUtil.ok(fileName);
        }

        RespData dataResp = (RespData) ttsMap.get("data");
        if (retData) {
            // Return data directly
            dataResp.read(response);
        }

        if (retUrl) {
            if (qiniuService != null) {
                // Upload file to cloud service
                byte[] dataBytes = dataResp.getBytes();
                fileName = qiniuService.upload(dataBytes, fileName);

                fileName = qiniuConfig.getFileUrl(fileName);
            } else {
                fileName = fileHelper.getFileUrl(LocationEnum.Service, fileName);
            }
        }
        return RespUtil.ok(fileName);
    }

    @AccessLimited(count = 1)
    @ApiOperation("语音识别")
    @PostMapping("/asr")
    public Object asr(
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestBody(required = false) String body,
            @RequestParam(required = false) String uid
    ) throws IOException {
        JSONArray result = null;
        if (file != null) {
            LogUtil.info("/speech/asr", file.getOriginalFilename());

            // Check file extension
            if (!FileHelper.checkFileExt(file, fileExtArr)) {
                return RespUtil.resp(RespEnum.UNSUPPORTED_MEDIA_TYPE, StrUtil.join(fileExtArr, ", "));
            }

            long len = file.getSize();
            String b64Str = B64Util.encode(file.getBytes());
            String format = FileUtil.getFileExt(file.getOriginalFilename()).replace(".", "");

            // Call asr
            result = baiduService.asr(format, b64Str, len, uid);
        } else if (StringUtils.isNotEmpty(body)) {
            // Parse params
            ParamUtil paramUtil = new ParamUtil(body);
            Integer len = paramUtil.getInteger("size");
            String b64Str = paramUtil.getStr("audio");
            String format = paramUtil.getStr("format");

            if (StringUtils.isEmpty(uid)) {
                uid = paramUtil.getStr("uid");
            }

            // Call asr
            result = baiduService.asr(format, b64Str, len, uid);
        }

        return CollectionUtils.isEmpty(result) ? RespUtil.error() : RespUtil.ok(result.getString(0));
    }

    @AccessLimited(count = 1)
    @ApiOperation("智能聊天")
    @GetMapping("/chat")
    public Object chat(
            @RequestAttribute(required = false) String ip,
            @RequestParam String text,
            @RequestParam(required = false) String uid
    ) {
        LogUtil.info("/speech/chat", text);

        // Call chat
        JSONArray result = tulingService.chat(text, ip, uid);
        return RespUtil.ok(result.getJSONObject(0).getJSONObject("values").getString("text"));
    }

    @AccessLimited(count = 1)
    @ApiOperation("Walle接口：asr - chat - tts")
    @PostMapping("/walle")
    public Object walle(
            HttpServletResponse response,
            @RequestAttribute(required = false) String ip,
            @RequestPart(required = false) MultipartFile file,
            @RequestBody(required = false) String body,
            @RequestParam(required = false) String url,
            @RequestParam(required = false) String data,
            @RequestParam(required = false) String uid
    ) throws IOException {
        // params
        if (StringUtils.isNotEmpty(body)) {
            ParamUtil paramUtil = new ParamUtil(body);
            if (StringUtils.isEmpty(ip)) {
                ip = paramUtil.getStr("ip");
            }

            if (StringUtils.isEmpty(url)) {
                url = paramUtil.getStr("url");
            }
            if (StringUtils.isEmpty(data)) {
                data = paramUtil.getStr("data");
            }
            if (StringUtils.isEmpty(uid)) {
                uid = paramUtil.getStr("uid");
            }
        }

        // asr
        Map asrMap = (Map) asr(file, body, uid);
        String asrStr = MapUtil.getStr(asrMap, "msg");
        if (MapUtil.getInt(asrMap, "code") == RespEnum.ERROR.getCode() || StringUtils.isEmpty(asrStr)) {
            return RespUtil.error();
        }

        // 发送到后台管理页面
        SpeechSocket.sendMessage(asrStr);

        // chat
        Map chatMap = (Map) chat(ip, asrStr, uid);
        String chatStr = MapUtil.getStr(chatMap, "msg");
        if (StringUtils.isEmpty(chatStr)) {
            return RespUtil.error();
        }

        // 发送到后台管理页面
        SpeechSocket.sendMessage(chatStr);

        // tts
        Map<String, Object> ttsMap = (Map<String, Object>) tts(response, chatStr, url, data, uid);
        ttsMap.put("asr", asrStr);
        ttsMap.put("chat", chatStr);
        return ttsMap;
    }

    @AccessLimited(count = 10)
    @ApiOperation("查询信息")
    @GetMapping
    public Object info() {
        LogUtil.info("/speech/info");

        Map<String, Object> ret = RespUtil.ok();
        ret.put("items", null);
        return ret;
    }
}
