package com.starter.jext;

import com.starter.annotation.AccessLimited;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = {"Jext技术社区"})
@RestController
@RequestMapping("/jext")
public class JextController {
    @Autowired
    JextService jextService;

    @AccessLimited(count = 1)
    @ApiOperation("获取社区信息")
    @GetMapping(value = "")
    public Object info() {
        return jextService.getInfo(false);
    }
}
