package com.starter.controller;

import com.starter.exception.AccessLimitException;
import org.apache.shiro.ShiroException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.MessagingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;

@RestControllerAdvice
public class ExceptionController {
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ShiroException.class)
    public Object shiroExceptionHandler(ShiroException e) {
        return new HashMap<String, Object>() {{
            put("msg", e.getMessage());
        }};
    }

    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(value = AccessLimitException.class)
    public Object accessLimitExceptionHandler(AccessLimitException e) {
        return new HashMap<String, Object>() {{
            put("msg", e.getMessage());
        }};
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(value = {MessagingException.class})
    public Object mqExceptionHandler(Exception e) {
        return new HashMap<String, Object>() {{
            put("msg", e.getMessage());
        }};
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(value = {DataAccessException.class})
    public Object dbExceptionHandler(Exception e) {
        return new HashMap<String, Object>() {{
            put("msg", e.getMessage());
        }};
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(value = Exception.class)
    public Object exceptionHandler(Exception e) {
        return new HashMap<String, Object>() {{
            put("msg", e.getMessage());
        }};
    }
}
