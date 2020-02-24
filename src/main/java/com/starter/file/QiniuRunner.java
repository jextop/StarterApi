package com.starter.file;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.common.enc.Md5Util;
import com.common.util.LogUtil;
import com.qiniu.storage.model.FileInfo;
import com.starter.entity.File;
import com.starter.service.impl.FileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QiniuRunner implements ApplicationRunner {
    static final String QINIU_FLAG = "file_info_from_qiniu";

    @Autowired(required = false)
    QiniuService qiniuService;

    @Autowired
    FileServiceImpl fileService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (qiniuService == null) {
            return;
        }

        // 检查是否已经拉取过信息
        String md5Str = Md5Util.md5(QINIU_FLAG);
        if (fileService.getOne(new QueryWrapper<File>().eq("md5", md5Str)) != null) {
            LogUtil.info("Sync Qiniu skipped");
            return;
        }

        // 标记拉取信息
        fileService.save(new File() {{
            setName(QINIU_FLAG);
            setMd5(md5Str);
        }});

        // 拉取文件信息
        List<FileInfo> fileList = qiniuService.list();
        for (FileInfo file : fileList) {
            // 写入数据库
            try {
                fileService.save(new File() {{
                    setName(file.mimeType);
                    setCode(file.hash);
                    setMd5(file.md5);
                    setUrl(file.key);
                    setSize(file.fsize);
                    setLocation(LocationEnum.Qiniu.getId());
                }});
            } catch (DuplicateKeyException e) {
                LogUtil.error("Error when sync Qiniu", e.getMessage());
            }
        }
        LogUtil.info("Sync Qiniu", fileList.size());
    }
}
