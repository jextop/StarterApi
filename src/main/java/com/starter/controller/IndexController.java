package com.starter.controller;

import com.common.util.DateUtil;
import com.starter.annotation.AccessLimited;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/")
public class IndexController {
    @AccessLimited(count = 1)
    @ApiOperation("检查服务是否运行")
    @GetMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Object index(@RequestAttribute(required = false) String ip) {
        return new HashMap<String, Object>() {{
            put("ip", ip);
            put("msg", String.format("Hello, Starter! 你好，%s", this.getClass().getName()));
            put("date", DateUtil.format(new Date()));
        }};
    }
}
