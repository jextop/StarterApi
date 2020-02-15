package com.starter.service.impl;

import com.starter.entity.File;
import com.starter.mapper.FileMapper;
import com.starter.service.IFileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ding
 * @since 2020-02-15
 */
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements IFileService {

}
