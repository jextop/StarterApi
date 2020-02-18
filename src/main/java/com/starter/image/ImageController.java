package com.starter.image;

import com.common.util.LogUtil;
import com.starter.annotation.AccessLimited;
import com.starter.controller.FileController;
import com.starter.file.FileTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

@Api(tags = {"图片上传下载"})
@RestController
@RequestMapping("/image")
public class ImageController {
    public static final String[] fileExtArr = new String[]{".png", ".jpg", ".jpeg", ".bmp"};
    public static final String[] profileExtArr = new String[]{".json"};

    @Autowired
    FileController fileController;

    @AccessLimited(count = 1)
    @ApiOperation("下载图片")
    @GetMapping("/{name}")
    public Object download(HttpServletResponse response, @PathVariable("name") String name) {
        LogUtil.info("/image", name);
        return fileController.doDownload(response, name);
    }

    @AccessLimited(count = 1)
    @ApiOperation("上传图片")
    @PostMapping("/upload")
    public Object upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(name = "type", required = false) String typeStr
    ) {
        FileTypeEnum type = FileTypeEnum.get(typeStr);
        LogUtil.info("/image/upload: ", typeStr, type.getName(), file.getOriginalFilename());

        return fileController.doUpload(type, file, type == FileTypeEnum.Profile ? profileExtArr : fileExtArr);
    }

    @AccessLimited(count = 1)
    @ApiOperation("文件列表, {pageIndex: 1, pageSize: 2}")
    @PostMapping("/list")
    public Object list(@RequestBody String body) {
        LogUtil.info("/image/list", body);
        return fileController.doList(body, new FileTypeEnum[]{FileTypeEnum.Image});
    }
}
