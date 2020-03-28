package com.starter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.common.util.LogUtil;
import com.starter.annotation.AccessLimited;
import com.starter.entity.Log;
import com.starter.service.impl.LogServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;

/**
 * @author Ding
 * @since 2020-01-07
 */
@Api(tags = {"操作日志"})
@Controller
@RequestMapping("/log")
public class LogController {
    LogServiceImpl logService;

    @Autowired
    public LogController(LogServiceImpl logService) {
        this.logService = logService;
    }

    @AccessLimited(count = 1)
    @ApiOperation("记录日志")
    @GetMapping("/")
    public HashMap<String, Object> log(@RequestAttribute(required = false) String ip, @RequestParam(required = false) String text) {
        // Write a log to db
        Log log = new Log() {{
            setSummary(text);
            setIp(ip);
            setCreated(System.currentTimeMillis());
        }};

        boolean bSave = logService.save(log);
        LogUtil.info("Check db to insert log", bSave, log.getSummary());

        // Read log from db
        Log ret = logService.getOne(new QueryWrapper<Log>()
                .orderByDesc("id")
                .eq("summary", log.getSummary())
        );
        Integer count = logService.count();

        return new HashMap<String, Object>() {{
            put("chk", "db");
            put("msg", log.getSummary());
            put("save", bSave);
            put("count", count);
            put("status", log.getSummary().equals(ret.getSummary()));
        }};
    }
}
