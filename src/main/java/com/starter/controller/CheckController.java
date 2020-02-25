package com.starter.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.common.enc.Md5Util;
import com.common.http.RespData;
import com.common.util.CodeUtil;
import com.common.util.DateUtil;
import com.common.util.LogUtil;
import com.common.util.StrUtil;
import com.starter.ai.BaiduService;
import com.starter.ai.TulingService;
import com.starter.annotation.AccessLimited;
import com.starter.entity.Log;
import com.starter.entity.User;
import com.starter.file.FileTypeEnum;
import com.starter.file.QiniuConfig;
import com.starter.file.QiniuService;
import com.starter.http.HttpService;
import com.starter.http.LocationService;
import com.starter.jext.JextService;
import com.starter.job.QuartzJob;
import com.starter.mq.MqService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@Api(tags = {"运行状态"})
@RestController
@RequestMapping("/chk")
public class CheckController {
    @Autowired
    LogServiceImpl logService;

    @Autowired
    RedisService redisService;

    @Autowired
    MqService mqService;

    @Autowired
    HttpService httpService;

    @Autowired
    JextService jextService;

    @Autowired(required = false)
    QiniuService qiniuService;

    @Autowired(required = false)
    QiniuConfig qiniuConfig;

    @Autowired
    BaiduService baiduService;

    @Autowired
    TulingService tulingService;

    @Autowired
    LocationService locationService;

    @AccessLimited(count = 1)
    @ApiOperation("检查服务是否运行")
    @GetMapping
    public Object chk(@RequestAttribute(required = false) String ip, @RequestParam(required = false) String text) {
        return new HashMap<String, Object>() {{
            put("chk", "ok");
            put("msg", String.format("%s_消息", ip));
            put("date", DateUtil.format(new Date()));
            put("services", new ArrayList<Object>() {{
                add(db(ip));
                add(cache(ip));
                add(mq(ip));
                add(http());
                add(job());
                add(json(ip));
                add(file());
                add(tts());
                add(chat(ip, text));
                add(location(ip));
            }});
        }};
    }

    @AccessLimited(count = 1)
    @ApiOperation("检查数据库")
    @GetMapping("/db")
    public Object db(@RequestAttribute(required = false) String ip) {
        // Write a log to db
        Log log = new Log() {{
            setSummary(String.format("db_test_%s_%s_数据库", ip, DateUtil.format(new Date())));
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
    @GetMapping("/cache")
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
    @GetMapping("/mq")
    public Object mq(@RequestAttribute(required = false) String ip) {
        String msg = String.format("check mq from java, %s, 消息队列", ip);

        // to service
        mqService.sendQueue(new HashMap<String, Object>() {{
            put("msg", msg);
            put("date", DateUtil.format(new Date()));
        }});

        return new HashMap<String, Object>() {{
            put("chk", "mq");
            put("msg", msg);
        }};
    }

    @AccessLimited(count = 1)
    @ApiOperation("检查HTTP连接")
    @GetMapping("/http")
    public Object http() {
        String strCourse = httpService.sendHttpGet("https://edu.51cto.com/center/course/index/search?q=Jext%E6%8A%80%E6%9C%AF%E7%A4%BE%E5%8C%BA");
        String[] courses = StrUtil.parse(strCourse, "[1-9]\\d*人学习");

        return new HashMap<String, Object>() {{
            put("chk", "http");
            put("msg", courses);
        }};
    }

    @AccessLimited(count = 1)
    @ApiOperation("检查作业调度")
    @GetMapping("/job")
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
    @GetMapping("/json")
    public Object json(@RequestAttribute(required = false) String ip) {
        return new HashMap<String, Object>() {{
            put("chk", "json");
            put("msg", new User() {{
                setName("json");
                setTitle(String.format("%s_%s", ip, DateUtil.format(new Date())));
            }});
        }};
    }

    @AccessLimited(count = 1)
    @ApiOperation("七牛云存储")
    @GetMapping("/file")
    public Object file() {
        String msg;
        if (qiniuService == null) {
            msg = "not configured";
        } else {
            String key = qiniuService.upload("chk/file/qiniu".getBytes(), null);
            msg = key == null ? "fail to upload" : String.format("%s%s", qiniuConfig.getUrl(), key);
        }

        return new HashMap<String, Object>() {{
            put("chk", "file/qiniu");
            put("msg", msg);
        }};
    }

    @AccessLimited(count = 1)
    @ApiOperation("百度AI语音处理")
    @GetMapping("/ai/tts")
    public Object tts() {
        String text = "检查百度AI语音合成";
        FileTypeEnum type = FileTypeEnum.Audio;
        String fileName = String.format("%s%s.%s", type.getFlag(), Md5Util.md5(text), BaiduService.FILE_EXT);

        // Call baidu api
        RespData dataResp = baiduService.tts(text);
        byte[] dataBytes = dataResp.getBytes();

        // upload to cloud service
        if (qiniuService != null) {
            fileName = qiniuService.upload(dataBytes, fileName);
            fileName = qiniuConfig.getFileUrl(fileName);
        }

        String fileUrl = fileName;
        return new HashMap<String, Object>() {{
            put("chk", "ai/tts");
            put("msg", fileUrl);
        }};
    }

    @AccessLimited(count = 1)
    @ApiOperation("图灵机器人智能聊天")
    @GetMapping("/ai/chat")
    public Object chat(@RequestAttribute(required = false) String ip, @RequestParam(required = false) String text) {
        return new HashMap<String, Object>() {{
            put("chk", "ai/chat");
            put("msg", tulingService.chat(StrUtil.isEmpty(text) ? "天气" : text, ip));
        }};
    }

    @AccessLimited(count = 1)
    @ApiOperation("百度地址服务")
    @GetMapping("/location")
    public Object location(@RequestAttribute(required = false) String ip) {
        return new HashMap<String, Object>() {{
            put("chk", "location");
            put("msg", locationService.getAddress(ip));
        }};
    }
}
