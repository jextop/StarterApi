package com.starter.filter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author ding
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CorsFilterTest {
    @Autowired
    CorsFilter corsFilter;

    @Test
    public void testDoFilter() throws ServletException, IOException {
        // Mock
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(request.getHeader(any())).thenReturn("header");
        doNothing().when(response).setHeader(any(), any());
        doNothing().when(filterChain).doFilter(any(), any());

        // Verify
        corsFilter.doFilter(request, response, filterChain);

        verify(request, atLeastOnce()).getHeader(any());
        verify(response, atLeastOnce()).setHeader(any(), any());
        verify(filterChain, only()).doFilter(any(), any());
    }

    @Test
    public void testInit() throws ServletException {
        corsFilter.init(mock(FilterConfig.class));
    }

    @Test
    public void testDestroy() throws ServletException {
        corsFilter.destroy();
    }
}
