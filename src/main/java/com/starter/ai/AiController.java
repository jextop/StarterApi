package com.starter.ai;

import com.common.enc.Md5Util;
import com.common.http.RespData;
import com.common.http.RespEnum;
import com.common.http.RespUtil;
import com.common.util.LogUtil;
import com.common.util.MapUtil;
import com.starter.annotation.AccessLimited;
import com.starter.controller.FileController;
import com.starter.file.FileHelper;
import com.starter.file.FileTypeEnum;
import com.starter.http.HttpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Api(tags = {"AI云服务调用"})
@RestController
@RequestMapping("/ai")
public class AiController {
    @Autowired
    HttpService httpService;

    @Autowired
    BaiduService baiduService;

    @Autowired
    FileHelper fileHelper;

    @Autowired
    FileController fileController;

    @AccessLimited(count = 1)
    @ApiOperation("语音合成")
    @GetMapping("/tts")
    public Object tts(HttpServletResponse response, @RequestParam("text") String text) {
        LogUtil.info("/tts", text);
        FileTypeEnum type = FileTypeEnum.Audio;
        String fileName = String.format("%s%s.%s", type.getFlag(), Md5Util.md5(text), BaiduService.FILE_EXT);

        // Find existed file firstly
        Map<String, Object> ret = (Map<String, Object>) fileController.doDownload(response, fileName);
        if (MapUtil.getInt(ret, "code") == RespEnum.OK.getCode()) {
            return ret;
        }

        // Call baidu api
        RespData dataResp = baiduService.tts(text);

        // Save file to local storage
        try {
            fileHelper.save(dataResp.getBytes(), fileName);
        } catch (IOException e) {
            return RespUtil.resp(RespEnum.ERROR, e.getMessage());
        }

        // Return data
        try {
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(dataResp.getBytes());
        } catch (IOException e) {
            LogUtil.error("Error when tts", e.getMessage());
        }

        response.setContentLength(dataResp.getContentLength());
        response.setContentType(dataResp.getContentType());
        return RespUtil.respOK();
    }
}
