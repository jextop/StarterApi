package com.starter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.common.util.CodeUtil;
import com.common.util.LogUtil;
import com.common.util.StrUtil;
import com.starter.annotation.AccessLimited;
import com.starter.entity.Log;
import com.starter.entity.User;
import com.starter.http.HttpService;
import com.starter.job.QuartzJob;
import com.starter.mapper.LogMapper;
import com.starter.mq.ActiveMqService;
import com.starter.service.RedisService;
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
import java.util.Date;
import java.util.HashMap;

@RestController
@RequestMapping("/")
public class CheckController extends BaseController {
    @Autowired
    ActiveMqService activeMqService;

    @Autowired
    RedisService redisService;

    @Autowired
    LogMapper logMapper;

    @Autowired
    HttpService httpService;

    @AccessLimited(count = 1)
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
    @GetMapping(value = "/chk")
    public Object chk(@RequestAttribute(required = false) String ip) {
        return new HashMap<String, Object>() {{
            put("chk", "ok");
            put("msg", String.format("%s_消息", ip));
            put("date", new Date());
        }};
    }

    @AccessLimited(count = 1)
    @GetMapping(path = "/chk/mq")
    public Object mq(@RequestAttribute(required = false) String ip) {
        ip = String.format("check mq ip, %s, %s 消息队列", ip, new Date().toString());
        activeMqService.send(ip);

        final String msg = ip;
        return new HashMap<String, Object>() {{
            put("chk", "mq");
            put("msg", msg);
        }};
    }

    @AccessLimited(count = 1)
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
    @GetMapping(value = "/chk/db")
    public Object db(@RequestAttribute(required = false) String ip) {
        // Write a test log to db
        Log log = new Log() {{
            setSummary(String.format("db_test_%s_%s_数据库", ip, new Date().toString()));
        }};
        logMapper.insert(log);
        LogUtil.info("Check db to insert log", log.getSummary());

        // Read log from db
        Log ret = logMapper.selectOne(new QueryWrapper<Log>().orderByDesc("id").eq("summary", log.getSummary()));
        Integer count = logMapper.selectCount(null);

        return new HashMap<String, Object>() {{
            put("chk", "db");
            put("msg", log.getSummary());
            put("status", ret != null && log.getSummary().equals(ret.getSummary()));
            put("count", count);
        }};
    }

    @AccessLimited(count = 1)
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
            date = scheduler.scheduleJob(job, trigger);
            scheduler.startDelayed(1);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        final Date jobDate = date;
        return new HashMap<String, Object>() {{
            put("chk", "job");
            put("date", jobDate);
        }};
    }

    @AccessLimited(count = 1)
    @GetMapping(value = "/chk/json", produces = "application/json")
    public Object json(@RequestAttribute(required = false) String ip) {
        return new User() {{
            setName("json");
            setTitle(String.format("%s_%s", ip, new Date().toString()));
            setUpdated(LocalDateTime.now());
        }};
    }

    @AccessLimited(count = 1)
    @GetMapping(value = "/chk/http", produces = "application/json")
    public Object http() {
        String strCourse = httpService.sendHttpGet("https://edu.51cto.com/lecturer/13841865.html");
        String[] courses = StrUtil.parse(strCourse, "[1-9]\\d*人学习");

        return new HashMap<String, Object>() {{
            put("chk", "http");
            put("course", courses);
        }};
    }
}
