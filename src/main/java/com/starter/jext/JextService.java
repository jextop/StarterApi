package com.starter.jext;

import com.common.util.JsonUtil;
import com.common.util.StrUtil;
import com.starter.http.HttpService;
import com.starter.mq.ActiveMqService;
import com.starter.service.RedisService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JextService {
    public static final String INFO_KEY = "jext.info";

    @Autowired
    ActiveMqService activeMqService;

    @Autowired
    RedisService redisService;

    @Autowired
    HttpService httpService;

    public Map<String, Object> getInfo() {
        return getInfo(false);
    }

    public Map<String, Object> getInfo(boolean updateCache) {
        Map<String, Object> infoMap = updateCache ? null : JsonUtil.parseObj(redisService.getStr(INFO_KEY));
        if (!MapUtils.isEmpty(infoMap)) {
            // Send mq
            activeMqService.sendQueue("jext.info");
            return infoMap;
        }

        // Get info
        String strCourse = httpService.sendHttpGet("https://edu.51cto.com/center/course/index/search?q=Jext%E6%8A%80%E6%9C%AF%E7%A4%BE%E5%8C%BA");
        String[] courses = StrUtil.parse(strCourse, "课程：[1-9]\\d*门");
        String[] users = StrUtil.parse(strCourse, "学员数量：[1-9]\\d*人");
        String[] userDetails = StrUtil.parse(strCourse, "[1-9]\\d*人学习");

        String strBlog = httpService.sendHttpGet("https://blog.51cto.com/13851865");
        String[] blogs = StrUtil.parse(strBlog, "<span>[1-9]\\d*</span>");
        String[] readers = StrUtil.parse(strBlog, "阅读&nbsp;[1-9]\\d*");

        infoMap = new HashMap<String, Object>() {{
            put("course", new HashMap<Object, Object>() {{
                put("count", courses != null && courses.length > 0 ? courses[0] : "");
                put("userCount", users != null && users.length > 0 ? users[0] : "");
                put("user", userDetails);
            }});
            put("blog", new HashMap<Object, Object>() {{
                put("count", blogs);
                put("reader", readers);
            }});
        }};

        // Set cache
        redisService.setStr(INFO_KEY, JsonUtil.toStr(infoMap));
        return infoMap;
    }
}
