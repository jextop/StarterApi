package com.starter.config;

import com.starter.interceptor.AccessInterceptor;
import com.starter.interceptor.ClientIpInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    AccessInterceptor accessInterceptor;

    @Autowired
    ClientIpInterceptor clientIpInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(accessInterceptor).addPathPatterns("/**");
        registry.addInterceptor(clientIpInterceptor).addPathPatterns("/**");
    }
}
