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
        // Header: Content-Disposition: attachment;fileName=abc.txt
        Header[] headers = response.getHeaders("Content-Disposition");
        for (Header header : headers) {
            final String value = header.getValue();
            LogUtil.info("Header", header.getName(), "value", value);

            if (value.startsWith(fileNameFlag)) {
                fileName = value.substring(fileNameFlag.length(), value.length());
            }
        }

        // 判断响应状态
        if (response.getStatusLine().getStatusCode() >= 300) {
            throw new IOException("HTTP Request is not success, Response code is " + response.getStatusLine().getStatusCode());
        }

        HttpEntity entity = response.getEntity();
        if (entity == null) {
            throw new ClientProtocolException("Response contains no content");
        }

        bytes = EntityUtils.toByteArray(entity);
        return bytes;
    }
}
