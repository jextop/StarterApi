package com.starter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.common.enc.Md5Util;
import com.common.file.FileUtil;
import com.common.http.RespEnum;
import com.common.http.RespUtil;
import com.common.util.CodeUtil;
import com.common.util.EmptyUtil;
import com.common.util.LogUtil;
import com.common.util.StrUtil;
import com.starter.annotation.AccessLimited;
import com.starter.config.MultipartConfig;
import com.starter.config.ServerConfig;
import com.starter.file.FileHelper;
import com.starter.file.LocationEnum;
import com.starter.file.FileTypeEnum;
import com.starter.service.impl.FileServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = {"文件上传下载"})
@RestController
@RequestMapping("/file")
public class FileController {
    @Autowired
    MultipartConfig multipartConfig;

    @Autowired
    FileServiceImpl fileService;

    @Autowired
    ServerConfig serverConfig;

    @Autowired
    FileHelper fileHelper;

    @AccessLimited(count = 1)
    @ApiOperation("上传文件，支持一个或多个同时上传")
    @PostMapping("/upload")
    public Object upload(
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "files", required = false) MultipartFile[] files
    ) {
        if (file != null) {
            LogUtil.info("/file/upload", file.getOriginalFilename());
            return doUpload(FileTypeEnum.File, file, null);
        }

        if (!EmptyUtil.isEmpty(files)) {
            LogUtil.info("/file/upload", files.length);
            return doUpload(FileTypeEnum.File, files, null);
        }

        return RespUtil.badRequest();
    }

    public Map<String, Object> doUpload(FileTypeEnum type, MultipartFile[] files, String[] specifiedExtArr) {
        // Save files one by one
        List<Map<String, String>> urlList = new ArrayList<Map<String, String>>(files.length);
        for (MultipartFile file : files) {
            Map<String, Object> ret = doUpload(type, file, specifiedExtArr);
            if (RespEnum.OK.getCode() == (int) ret.get("code")) {
                urlList.add(new HashMap<String, String>() {{
                    put("url", (String) ret.get("url"));
                    put("name", (String) ret.get("name"));
                }});
            }
        }

        // Return file names
        Map<String, Object> ret = RespUtil.respOK();
        ret.put("files", urlList);
        return ret;
    }

    public Map<String, Object> doUpload(FileTypeEnum type, MultipartFile file, String[] specifiedExtArr) {
        if (file == null || file.isEmpty()) {
            return RespUtil.resp(RespEnum.FILE_EMPTY);
        }

        // Check file extension
        if (!checkFileExt(file, specifiedExtArr)) {
            return RespUtil.resp(RespEnum.UNSUPPORTED_MEDIA_TYPE, StrUtil.join(specifiedExtArr, ", "));
        }

        // Read file
        byte[] fileBytes = null;
        try {
            fileBytes = file.getBytes();
        } catch (IOException e) {
            return RespUtil.resp(RespEnum.ERROR, e.getMessage());
        }

        // Get md5 and check duplicated files
        String md5Str = Md5Util.md5(fileBytes);
        com.starter.entity.File fileDb = fileService.getOne(
                new QueryWrapper<com.starter.entity.File>().eq("md5", md5Str)
        );

        if (fileDb != null) {
            LogUtil.info("Existed file in db", file.getOriginalFilename(), md5Str);

            Map<String, Object> ret = RespUtil.respOK();
            ret.put("name", file.getName());
            ret.put("url", fileHelper.getFileUrl(fileDb));
            return ret;
        }

        // todo: support cloud storage service

        // New file: remember the file name and use code as new name to save
        String name = FileUtil.getFileName(file.getOriginalFilename());
        String fileExt = FileUtil.getFileExt(name);

        String code = String.format("%s%s", type.getFlag(), CodeUtil.getCode());
        String fileName = String.format("%s%s", code, fileExt);

        // Save file
        try {
            fileHelper.save(fileBytes, fileName);
        } catch (IOException e) {
            return RespUtil.resp(RespEnum.ERROR, e.getMessage());
        }

        // Add file info to db
        fileDb = new com.starter.entity.File() {{
            setName(name);
            setCode(code);
            setMd5(md5Str);
            setUrl(fileName);
            setSize(file.getSize());
            setFileType(type.getId());
            setLocation(LocationEnum.Service.getId());
        }};
        fileService.save(fileDb);

        Map<String, Object> ret = RespUtil.respOK();
        ret.put("name", name);
        ret.put("url", fileHelper.getFileUrl(fileDb));
        return ret;
    }

    private boolean checkFileExt(MultipartFile file, String[] specifiedExtArr) {
        if (EmptyUtil.isEmpty(specifiedExtArr)) {
            return true;
        }

        String fileExt = FileUtil.getFileExt(file.getOriginalFilename());
        if (!StrUtil.isEmpty(fileExt)) {
            for (String ext : specifiedExtArr) {
                if (ext.equalsIgnoreCase(fileExt)) {
                    return true;
                }
            }
        }
        return false;
    }

    @AccessLimited(count = 1)
    @ApiOperation("下载文件")
    @GetMapping("/{name}")
    public Object download(HttpServletResponse response, @PathVariable("name") String name) {
        LogUtil.info("/file", name);
        return doDownload(response, name);
    }

    public Object doDownload(HttpServletResponse response, String name) {
        // Get file info from db
        String code = FileUtil.removeFileExt(name);
        com.starter.entity.File fileDb = fileService.getOne(
                new QueryWrapper<com.starter.entity.File>().eq("code", code)
        );

        if (FileHelper.isValid(fileDb)) {
            // todo: support cloud storage service
            if (fileDb.getLocation() != LocationEnum.Service.getId()) {
                LogUtil.info(String.format("Cloud storage file: %s", name));
                return RespUtil.notImplemented();
            }

            // Set file name
            String fileName = fileDb.getName();
            response.addHeader("Content-Disposition", String.format("attachment;fileName=%s", fileName));
        }

        // Find the saved file
        String filePath = fileHelper.getFilePath(name);
        File file = new File(filePath, name);
        if (!file.exists()) {
            LogUtil.info(String.format("Un existed file: %s", name));
            return RespUtil.resp(RespEnum.NOT_FOUND);
        }

        // Read file
        response.setContentType("application/octet-stream");
        fileHelper.read(response, file);
        return RespUtil.respOK();
    }
}
