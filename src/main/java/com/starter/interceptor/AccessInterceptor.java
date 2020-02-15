package com.starter.interceptor;

import com.common.util.LogUtil;
import com.common.http.ReqUtil;
import com.starter.annotation.AccessLimited;
import com.starter.exception.AccessLimitException;
import com.starter.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

@Component
public class AccessInterceptor implements HandlerInterceptor {
    @Autowired
    private RedisService redisService;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler
    ) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        final HandlerMethod handlerMethod = (HandlerMethod) handler;
        final Method method = handlerMethod.getMethod();
        final AccessLimited accessLimited = method.getAnnotation(AccessLimited.class);
        if (accessLimited == null) {
            return true;
        }

        String key = String.format("%s%s_%s:%s",
                !accessLimited.ip() ? "" : ReqUtil.getIP(request),
                !accessLimited.session() ? "" : request.getSession().getId(),
                request.getMethod(),
                request.getRequestURI()
        );
        try {
            long count = redisService.incr(key);
            if (count <= accessLimited.count()) {
                if (count == 1) {
                    redisService.expire(key, accessLimited.seconds());
                }
                return true;
            }
        } catch (RedisConnectionFailureException e) {
            LogUtil.error(e.getMessage());
            return true;
        }
        throw new AccessLimitException();
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
    }
}
