package com.starter.file;

import com.common.file.FileUtil;
import com.common.util.LogUtil;
import com.starter.config.MultipartConfig;
import com.starter.config.ServerConfig;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class FileHelper {
    @Autowired
    MultipartConfig multipartConfig;

    @Autowired
    ServerConfig serverConfig;

    @Autowired(required = false)
    QiniuConfig qiniuConfig;

    public String getFileUrl(com.starter.entity.File file) {
        return file == null ? null : getFileUrl(LocationEnum.get(file.getLocation()), file.getUrl());
    }

    public String getFileUrl(LocationEnum location, String fileName) {
        if (location == null) {
            return fileName;
        }

        if (location == LocationEnum.Service) {
            String specifiedUrl = getFileType(fileName).getName();
            String serverUrl = serverConfig.getServerUrl();
            return String.format("%s/%s/%s", serverUrl, specifiedUrl, fileName);
        } else if (location == LocationEnum.Qiniu && qiniuConfig != null) {
            return qiniuConfig.getFileUrl(fileName);
        }
        return fileName;
    }

    public FileTypeEnum getFileType(String fileName) {
        return StringUtils.isEmpty(fileName) ? FileTypeEnum.File : FileTypeEnum.getByFlag(fileName.substring(0, 1));
    }

    public String getFilePath(String fileName) {
        String subPath = getFileType(fileName).getName();
        String filePath = multipartConfig.getLocation();
        File file = new File(filePath, subPath);
        file.mkdirs();
        return file.getPath();
    }

    public void fillInfo(List<com.starter.entity.File> itemList) {
        if (CollectionUtils.isEmpty(itemList)) {
            return;
        }

        // Set full url
        for (com.starter.entity.File item : itemList) {
            item.setUrl(getFileUrl(item));
        }
    }

    public String save(byte[] bytes, String fileName) throws IOException {
        if (bytes == null || bytes.length <= 0 || fileName == null || fileName.isEmpty()) {
            return null;
        }

        // Check path
        String filePath = getFilePath(fileName);
        File file = new File(filePath);
        if (!file.exists() && !file.mkdirs()) {
            return null;
        }

        // Check file existed or not. Note it's unique.
        file = new File(filePath, fileName);
        if (file.exists()) {
            return file.getPath();
        }

        // Write to disc
        Path path = Paths.get(filePath, fileName);
        Files.write(path, bytes);
        LogUtil.info("Success save", path.toString());
        return path.toString();
    }

    public void read(HttpServletResponse response, File file) {
        // Read file
        FileInputStream fileStream = null;
        BufferedInputStream bufferStream = null;
        try {
            fileStream = new FileInputStream(file);
            bufferStream = new BufferedInputStream(fileStream);
            OutputStream outputStream = response.getOutputStream();

            byte[] buffer = new byte[1024 * 100];
            int count;
            while ((count = bufferStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, count);
            }

            count = (int) file.length();
            response.setContentLength(count);
            LogUtil.info("Success download", file.getPath(), count);
        } catch (Exception e) {
            LogUtil.error("Error when download", file.getPath(), e.getMessage());
        } finally {
            closeStream(bufferStream);
            closeStream(fileStream);
        }
    }

    private void closeStream(InputStream stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean isValid(com.starter.entity.File file) {
        return file != null && file.getCode() != null && !file.getCode().isEmpty()
                && file.getName() != null && !file.getName().isEmpty()
                && file.getUrl() != null && !file.getUrl().isEmpty()
                && file.getMd5() != null && !file.getMd5().isEmpty();
    }

    public static boolean checkFileExt(MultipartFile file, String[] specifiedExtArr) {
        if (ArrayUtils.isEmpty(specifiedExtArr)) {
            return true;
        }

        String fileExt = FileUtil.getFileExt(file.getOriginalFilename());
        if (StringUtils.isNotEmpty(fileExt)) {
            for (String ext : specifiedExtArr) {
                if (ext.equalsIgnoreCase(fileExt)) {
                    return true;
                }
            }
        }
        return false;
    }
}
