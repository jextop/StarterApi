package com.starter.service.impl;

import com.starter.entity.Auth;
import com.starter.mapper.AuthMapper;
import com.starter.service.IAuthService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Ding
 * @since 2020-01-28
 */
@Service
public class AuthServiceImpl extends ServiceImpl<AuthMapper, Auth> implements IAuthService {

}
