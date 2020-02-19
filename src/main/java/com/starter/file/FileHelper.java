package com.starter.file;

import com.common.file.FileUtil;
import com.common.util.EmptyUtil;
import com.common.util.LogUtil;
import com.common.util.StrUtil;
import com.starter.config.MultipartConfig;
import com.starter.config.ServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public String getFileUrl(com.starter.entity.File file) {
        String url = file.getUrl();
        if (!StrUtil.isEmpty(url)) {
            Integer location = file.getLocation();
            if (location != null && location == LocationEnum.Service.getId()) {
                String serverUrl = serverConfig.getServerUrl();
                String specifiedUrl = FileTypeEnum.get(file.getFileType()).getName();
                url = String.format("%s/%s/%s%s", serverUrl, specifiedUrl, file.getCode(), FileUtil.getFileExt(url));
            }
        }
        return url;
    }

    public String getFilePath(String fileName) {
        FileTypeEnum type = FileTypeEnum.getByFlag(fileName.substring(0, 1));
        String subPath = type == null ? "tmp" : type.getName();

        String filePath = multipartConfig.getLocation();
        File file = new File(filePath, subPath);
        file.mkdirs();
        return file.getPath();
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

        // Get file name. Note it's unique code.
        Path path = Paths.get(filePath, fileName);

        // Write to disc
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
            int i;
            while ((i = bufferStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, i);
            }
            LogUtil.info("Success download", file.getPath());
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

    public void fillInfo(List<com.starter.entity.File> itemList) {
        if (EmptyUtil.isEmpty(itemList)) {
            return;
        }

        // Set full url
        for (com.starter.entity.File item : itemList) {
            item.setUrl(getFileUrl(item));
        }
    }
}
