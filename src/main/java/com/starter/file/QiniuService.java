package com.starter.file;

import com.common.util.JsonUtil;
import com.common.util.LogUtil;
import com.common.util.StrUtil;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@ConditionalOnBean(QiniuConfig.class)
public class QiniuService {
    @Autowired
    BucketManager bucketManager;

    @Autowired
    UploadManager uploadManager;

    @Autowired
    Auth auth;

    @Autowired
    QiniuConfig config;

    public List<FileInfo> list() {
        //文件名前缀
        String prefix = "";
        //每次迭代的长度限制，最大1000，推荐值 1000
        int limit = 1000;
        //指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
        String delimiter = "";

        //列举空间文件列表
        List<FileInfo> fileList = new ArrayList<FileInfo>();
        BucketManager.FileListIterator iterator = bucketManager.createFileListIterator(config.getBucket(), prefix, limit, delimiter);
        while (iterator.hasNext()) {
            //处理获取的file list结果
            FileInfo[] items = iterator.next();
            if (ArrayUtils.isNotEmpty(items)) {
                fileList.addAll(Arrays.asList(items));
            }
        }
        return fileList;
    }

    public <T> String upload(T filePathOrData, String fileName) {
        // 生成上传凭证
        String upToken = auth.uploadToken(config.getBucket());

        // 上传文件
        try {
            Response response;
            if (filePathOrData instanceof String) {
                String filePath = (String) filePathOrData;
                response = uploadManager.put(filePath, fileName, upToken);
            } else if (filePathOrData instanceof File) {
                File file = (File) filePathOrData;
                response = uploadManager.put(file, fileName, upToken);
            } else if (filePathOrData instanceof InputStream) {
                InputStream stream = (InputStream) filePathOrData;
                response = uploadManager.put(stream, fileName, upToken, null, null);
            } else if (filePathOrData instanceof byte[]) {
                byte[] data = (byte[]) filePathOrData;
                response = uploadManager.put(data, fileName, upToken);
            } else {
                return null;
            }

            // 解析返回结果
            DefaultPutRet putRet = JsonUtil.parseObj(response.bodyString(), DefaultPutRet.class);
            return putRet == null ? null : putRet.key;
        } catch (QiniuException ex) {
            Response r = ex.response;
            LogUtil.error("Error when upload to qiniu", r.toString());
        }
        return null;
    }

    public String uploadFile(String filePath, String fileName) {
        if (StringUtils.isEmpty(filePath) || !new File(filePath).exists()) {
            return null;
        }

        // 生成上传凭证
        String upToken = auth.uploadToken(config.getBucket());

        try {
            // 上传文件
            Response response = uploadManager.put(filePath, fileName, upToken);
            DefaultPutRet putRet = JsonUtil.parseObj(response.bodyString(), DefaultPutRet.class);
            if (putRet != null) {
                return putRet.key;
            }
        } catch (QiniuException ex) {
            Response r = ex.response;
            LogUtil.error("Error when upload to qiniu", r.toString());
        }
        return null;
    }
}
