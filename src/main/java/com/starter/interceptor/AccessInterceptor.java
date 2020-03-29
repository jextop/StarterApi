package com.starter.interceptor;

import com.common.http.ReqUtil;
import com.common.util.LogUtil;
import com.starter.annotation.AccessLimited;
import com.starter.cache.CacheService;
import com.starter.exception.AccessLimitException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author ding
 */
@Component
public class AccessInterceptor implements HandlerInterceptor {
    private CacheService cacheService;

    @Autowired
    public AccessInterceptor(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler
    ) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Method method = handlerMethod.getMethod();
        AccessLimited accessLimited = method.getAnnotation(AccessLimited.class);
        if (accessLimited == null) {
            return true;
        }

        String key = String.format("%s%s_%s:%s",
                !accessLimited.ip() ? "" : ReqUtil.getIp(request),
                !accessLimited.session() ? "" : request.getSession().getId(),
                request.getMethod(),
                request.getRequestURI()
        );
        try {
            long count = cacheService.incr(key);
            if (count <= accessLimited.count()) {
                if (count == 1) {
                    cacheService.expire(key, accessLimited.seconds());
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
