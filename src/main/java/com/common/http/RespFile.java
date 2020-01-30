package com.common.http;

import com.common.file.FileUtil;
import com.common.util.LogUtil;
import com.common.util.StrUtil;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class RespFile implements ResponseHandler<byte[]> {
    private static final String fileNameFlag = "attachment;fileName=";

    private byte[] bytes;
    private String fileName;

    public byte[] getBytes() {
        return bytes;
    }

    public String getFileName() {
        return fileName;
    }

    public boolean isValid() {
        return !StrUtil.isEmpty(fileName) && bytes != null && bytes.length > 0;
    }

    public String saveFile(String filePath) {
        if (StrUtil.isEmpty(filePath)) {
            filePath = fileName;
        }
        if (StrUtil.isEmpty(filePath) || bytes == null || bytes.length <= 0) {
            return null;
        }

        if (FileUtil.write(filePath, bytes)) {
            return filePath;
        }

        LogUtil.error("Fail to save file", fileName, filePath);
        return null;
    }

    @Override
    public byte[] handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
        // 判断响应状态
        if (response.getStatusLine().getStatusCode() >= 300) {
            throw new IOException("HTTP Request is not success, Response code is " + response.getStatusLine().getStatusCode());
        }

        // 读取文件名称，Header: Content-Disposition: attachment;fileName=abc.txt
        Header header = response.getFirstHeader("Content-Disposition");
        String headerValue = header.getValue();
        LogUtil.info("Header", header.getName(), "value", headerValue);

        if (headerValue.startsWith(fileNameFlag)) {
            fileName = headerValue.substring(fileNameFlag.length(), headerValue.length());
        }

        // 读取返回内容
        HttpEntity entity = response.getEntity();
        if (entity == null) {
            throw new ClientProtocolException("Response contains no content");
        }

        bytes = EntityUtils.toByteArray(entity);
        return bytes;
    }
}
