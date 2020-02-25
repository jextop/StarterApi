package com.starter.speech;

import com.alibaba.fastjson.JSONArray;
import com.common.enc.B64Util;
import com.common.enc.Md5Util;
import com.common.file.FileUtil;
import com.common.http.RespData;
import com.common.http.RespEnum;
import com.common.http.RespUtil;
import com.common.util.LogUtil;
import com.common.util.StrUtil;
import com.starter.annotation.AccessLimited;
import com.starter.file.FileHelper;
import com.starter.file.FileTypeEnum;
import com.starter.file.QiniuConfig;
import com.starter.file.QiniuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

@Api(tags = {"AI云服务调用"})
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
    public Object tts(HttpServletResponse response, @RequestParam("text") String text) {
        LogUtil.info("/speech/tts", text);
        FileTypeEnum type = FileTypeEnum.Audio;
        String fileName = String.format("%s%s.%s", type.getFlag(), Md5Util.md5(text), BaiduService.FILE_EXT);

        // Find the saved file
        String filePath = fileHelper.getFilePath(fileName);
        File file = new File(filePath, fileName);
        if (file.exists()) {
            // Read file
            fileHelper.read(response, file);

            if (qiniuConfig != null) {
                fileName = qiniuConfig.getFileUrl(fileName);
            }
            return RespUtil.ok(fileName);
        }

        // Call baidu api
        RespData dataResp = baiduService.tts(text);
        byte[] dataBytes = dataResp.getBytes();

        // Save file to local storage
        try {
            fileHelper.save(dataBytes, fileName);
        } catch (IOException e) {
            LogUtil.error("Error when save tts data", e.getMessage());
        }

        // upload to cloud service
        if (qiniuService != null) {
            fileName = qiniuService.upload(dataBytes, fileName);
            fileName = qiniuConfig.getFileUrl(fileName);
        }

        // Return data directly
        try {
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(dataResp.getBytes());
        } catch (IOException e) {
            LogUtil.error("Error when return tts", e.getMessage());
            return RespUtil.resp(RespEnum.ERROR, e.getMessage());
        }

        response.setContentLength(dataResp.getContentLength());
        response.setContentType(dataResp.getContentType());
        return RespUtil.ok(fileName);
    }

    @AccessLimited(count = 1)
    @ApiOperation("语音识别")
    @PostMapping("/asr")
    public Object asr(@RequestPart("file") MultipartFile file) throws IOException {
        LogUtil.info("/speech/asr", file.getOriginalFilename());

        // Check file extension
        if (!FileHelper.checkFileExt(file, fileExtArr)) {
            return RespUtil.resp(RespEnum.UNSUPPORTED_MEDIA_TYPE, StrUtil.join(fileExtArr, ", "));
        }

        long len = file.getSize();
        String b64Str = B64Util.encode(file.getBytes());
        String format = FileUtil.getFileExt(file.getOriginalFilename()).replace(".", "");

        // Call asr
        JSONArray result = baiduService.asr(format, b64Str, len);
        return RespUtil.ok(result.getString(0));
    }

    @AccessLimited(count = 1)
    @ApiOperation("智能聊天")
    @GetMapping("/chat")
    public Object chat(@RequestAttribute(required = false) String ip, @RequestParam("text") String text) {
        LogUtil.info("/speech/chat", text);

        // Call chat
        JSONArray result = tulingService.chat(text, ip);
        return RespUtil.ok(result.getJSONObject(0).getJSONObject("values").getString("text"));
    }
}
