package com.starter.interceptor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ClientIpInterceptorTest {
    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    HandlerMethod handler;

    ClientIpInterceptor interceptor;

    @BeforeEach
    public void initMock() {
        MockitoAnnotations.initMocks(this);

        interceptor = new ClientIpInterceptor();
    }

    @Test
    public void testPreHandle() throws Exception {
        interceptor.preHandle(request, response, handler);
        verify(request, times(1)).setAttribute(any(), any());
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
