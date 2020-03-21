package com.starter.interceptor;

import com.starter.annotation.AccessLimited;
import com.starter.cache.CacheService;
import com.starter.exception.AccessLimitException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccessInterceptorTest {
    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    HandlerMethod handler;

    @Mock
    CacheService cacheService;

    AccessInterceptor interceptor;

    @BeforeEach
    public void initMock() {
        MockitoAnnotations.initMocks(this);

        interceptor = new AccessInterceptor(cacheService);
    }

    @AccessLimited(session = false)
    @Test
    public void testPreHandle() throws Exception {
        // Mock
        Method method = AccessInterceptorTest.class.getMethod("testPreHandle");
        when(handler.getMethod()).thenReturn(method);
        when(cacheService.incr(any())).thenReturn(1L);

        // Verify
        Assertions.assertTrue(interceptor.preHandle(request, response, handler));

        // Exceed the limitation
        when(cacheService.incr(any())).thenReturn(Long.MAX_VALUE);
        Assertions.assertThrows(AccessLimitException.class, () -> interceptor.preHandle(request, response, handler));

        // Cache service exception
        doThrow(new RedisConnectionFailureException("unit testing mock")).when(cacheService).incr(any());
        Assertions.assertTrue(interceptor.preHandle(request, response, handler));

        // Not access limited
        method = AccessInterceptorTest.class.getMethod("initMock");
        when(handler.getMethod()).thenReturn(method);
        Assertions.assertTrue(interceptor.preHandle(request, response, handler));

        // Not HandlerMethod
        Assertions.assertTrue(interceptor.preHandle(request, response, null));
    }

    @Test
    public void testPostHandle() throws Exception {
        // To keep 100% unit-testing coverage
        interceptor.postHandle(request, response, handler, mock(ModelAndView.class));
    }

    @Test
    public void testAfterCompletion() throws Exception {
        // To keep 100% unit-testing coverage
        interceptor.afterCompletion(request, response, handler, mock(Exception.class));
    }
}
