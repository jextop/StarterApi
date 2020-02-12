package com.starter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.common.util.CodeUtil;
import com.common.util.DateUtil;
import com.common.util.LogUtil;
import com.common.util.StrUtil;
import com.starter.annotation.AccessLimited;
import com.starter.entity.Log;
import com.starter.entity.User;
import com.starter.http.HttpService;
import com.starter.jext.JextService;
import com.starter.job.QuartzJob;
import com.starter.mq.ActiveMqService;
import com.starter.service.RedisService;
import com.starter.service.impl.LogServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SimpleTrigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@Api(tags = {"运行状态"})
@RestController
@RequestMapping("/")
public class CheckController {
    @Autowired
    ActiveMqService activeMqService;

    @Autowired
    RedisService redisService;

    @Autowired
    LogServiceImpl logService;

    @Autowired
    HttpService httpService;

    @Autowired
    JextService jextService;

    @AccessLimited(count = 1)
    @ApiOperation("检查服务是否运行")
    @GetMapping(value = "")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Object index(@RequestAttribute(required = false) String ip) {
        return new HashMap<String, Object>() {{
            put("ip", ip);
            put("msg", String.format("Hello, Starter! 你好，%s", this.getClass().getName()));
            put("date", new Date());
        }};
    }

    @AccessLimited(count = 1)
    @ApiOperation("检查服务是否运行")
    @GetMapping(value = "/chk")
    public Object chk(@RequestAttribute(required = false) String ip) {
        return new HashMap<String, Object>() {{
            put("chk", "ok");
            put("msg", String.format("%s_消息", ip));
            put("date", DateUtil.format(new Date()));
            put("services", new ArrayList<Object>(){{
                add(db(ip));
                add(cache(ip));
                add(mq(ip));
                add(http());
                add(job());
                add(json(ip));
            }});
        }};
    }

    @AccessLimited(count = 1)
    @ApiOperation("检查数据库")
    @GetMapping(value = "/chk/db")
    public Object db(@RequestAttribute(required = false) String ip) {
        // Write a log to db
        Log log = new Log() {{
            setSummary(String.format("db_test_%s_%s_数据库", ip, new Date()));
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
            put("status", log.getSummary().equals(ret.getSummary()));
            put("count", count);
        }};
    }

    @AccessLimited(count = 1)
    @ApiOperation("检查缓存系统")
    @GetMapping(value = "/chk/cache")
    public Object cache(@RequestAttribute(required = false) String ip) {
        // Get a unique key
        String key = String.format("cache_test_%s_%s_缓存", ip, CodeUtil.getCode());

        // Set cache
        redisService.setStr(key, key, 3);

        // Get cache
        String str = redisService.getStr(key);
        LogUtil.info("Check cache to set str", key, str);

        // Delete key
        redisService.delStr(key);

        return new HashMap<String, Object>() {{
            put("chk", "cache");
            put("msg", str);
            put("status", key.equals(str));
        }};
    }

    @AccessLimited(count = 1)
    @ApiOperation("检查消息队列")
    @GetMapping(path = "/chk/mq")
    public Object mq(@RequestAttribute(required = false) String ip) {
        String msg = String.format("check mq, %s, %s 消息队列", ip, new Date().toString());
        activeMqService.send(msg);

        return new HashMap<String, Object>() {{
            put("chk", "mq");
            put("msg", msg);
        }};
    }

    @AccessLimited(count = 1)
    @ApiOperation("检查HTTP连接")
    @GetMapping(value = "/chk/http", produces = "application/json")
    public Object http() {
        String strCourse = httpService.sendHttpGet("https://edu.51cto.com/lecturer/13841865.html");
        String[] courses = StrUtil.parse(strCourse, "[1-9]\\d*人学习");

        return new HashMap<String, Object>() {{
            put("chk", "http");
            put("msg", courses);
        }};
    }

    @AccessLimited(count = 1)
    @ApiOperation("检查作业调度")
    @GetMapping(value = "/chk/job")
    public Object job() {
        JobDetail job = JobBuilder.newJob(QuartzJob.class).build();
        SimpleTrigger trigger = (SimpleTrigger) TriggerBuilder.newTrigger()
                .forJob(job)
                .startAt(new Date())
                .build();

        Date date = null;
        try {
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.getContext().put("jextService", jextService);

            date = scheduler.scheduleJob(job, trigger);
            scheduler.startDelayed(1);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        final Date jobDate = date;
        return new HashMap<String, Object>() {{
            put("chk", "job");
            put("msg", DateUtil.format(jobDate));
        }};
    }

    @AccessLimited(count = 1)
    @ApiOperation("检查JSON数据传输")
    @GetMapping(value = "/chk/json", produces = "application/json")
    public Object json(@RequestAttribute(required = false) String ip) {
        return new HashMap<String, Object>() {{
            put("chk", "job");
            put("msg", new User() {{
                setName("json");
                setTitle(String.format("%s_%s", ip, DateUtil.format(new Date())));
                setUpdated(LocalDateTime.now());
            }});
        }};
    }
}
