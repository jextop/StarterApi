package com.starter.controller;

import com.common.http.RespEnum;
import com.common.http.RespUtil;
import com.starter.exception.AccessLimitException;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.shiro.ShiroException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.MessagingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

@RestControllerAdvice
public class ExceptionController {
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ShiroException.class)
    public Object shiroExceptionHandler(ShiroException e) {
        return RespUtil.resp(RespEnum.UNAUTHORIZED, e.getMessage());
    }

    @ResponseStatus(HttpStatus.TOO_MANY_REQUESTS)
    @ExceptionHandler(AccessLimitException.class)
    public Object accessLimitExceptionHandler(AccessLimitException e) {
        return RespUtil.resp(RespEnum.TOO_MANY_REQUESTS, e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    @ExceptionHandler(MultipartException.class)
    public Object fileExceptionHandler(MultipartException e) {
        return RespUtil.resp(RespEnum.UNSUPPORTED_MEDIA_TYPE, e.getMessage());
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler({PersistenceException.class, DataAccessException.class, MessagingException.class})
    public Object dataExceptionHandler(RuntimeException e) {
        return RespUtil.resp(RespEnum.SERVICE_UNAVAILABLE, e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Object exceptionHandler(Exception e) {
        return RespUtil.resp(RespEnum.ERROR, e.getMessage());
    }
}
