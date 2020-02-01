package com.starter.jext;

import com.common.util.JsonUtil;
import com.common.util.StrUtil;
import com.starter.http.HttpService;
import com.starter.mq.ActiveMqService;
import com.starter.service.RedisService;
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
            activeMqService.send("jext.info");
            return infoMap;
        }

        // Get info
        String str51CtoCourse = httpService.sendHttpGet("https://edu.51cto.com/center/course/index/search?q=Jext%E6%8A%80%E6%9C%AF%E7%A4%BE%E5%8C%BA");
        String[] course51Cto = StrUtil.parse(str51CtoCourse, "课程：[1-9]\\d*门");
        String[] user51Cto = StrUtil.parse(str51CtoCourse, "学员数量：[1-9]\\d*人");
        String[] userDetail51Cto = StrUtil.parse(str51CtoCourse, "[1-9]\\d*人学习");

        String str51CtoBlog = httpService.sendHttpGet("https://blog.51cto.com/13851865");
        String[] blog51Cto = StrUtil.parse(str51CtoBlog, "<span>[1-9]\\d*(W\\+)*</span>");
        String[] reader51Cto = StrUtil.parse(str51CtoBlog, "阅读&nbsp;[1-9]\\d*(W\\+)*");

        String strCsdnCourse = httpService.sendHttpGet("https://edu.csdn.net/lecturer/4306");
        String[] userCsdn = StrUtil.parse(strCsdnCourse, "累计<b>[1-9]\\d*</b>人");
        String[] userDetailCsdn = StrUtil.parse(strCsdnCourse, "<span>[1-9]\\d*人学习过</span>");

        String strCsdnBlog = httpService.sendHttpGet("https://blog.csdn.net/xiziyidi");
        String[] blogCsdn = StrUtil.parse(strCsdnBlog, "<span class=\"count\">[1-9]\\d*(\\W\\+)*</span>");
        String[] readerCsdn = StrUtil.parse(strCsdnBlog, "<span class=\"num\">[1-9]\\d*(\\W\\+)*</span>");
        String[] rankCsdn = StrUtil.parse(strCsdnBlog, "<a class=\"grade-box-rankA\" href=\"https://blog.csdn.net/rank/writing_rank\\w*\" target=\"_blank\">\\s*[1-9]\\d*(\\W\\+)*\\s*</a>");
        String[] scoreCsdn = StrUtil.parse(strCsdnBlog, "<dd title=\"[1-9]\\d*(\\W\\+)*\">");

        infoMap = new HashMap<String, Object>() {{
            put("cto51", new HashMap<Object, Object>() {{
                put("course", new HashMap<Object, Object>() {{
                    put("count", formatInfo(formatInfo(course51Cto, "课程："), "门"));
                    put("userCount", formatInfo(formatInfo(user51Cto, "学员数量："), "人"));
                    put("user", formatInfo(userDetail51Cto, "人学习"));
                }});
                put("blog", new HashMap<Object, Object>() {{
                    put("count", formatInfo(formatInfo(blog51Cto, "<span>"), "</span>"));
                    put("reader", formatInfo(reader51Cto, "阅读&nbsp;"));
                }});
            }});
            put("csdn", new HashMap<Object, Object>() {{
                put("course", new HashMap<Object, Object>() {{
                    put("userCount", formatInfo(formatInfo(userCsdn, "累计<b>"), "</b>人"));
                    put("user", formatInfo(formatInfo(userDetailCsdn, "<span>"), "人学习过</span>"));
                }});
                put("blog", new HashMap<Object, Object>() {{
                    put("count", formatInfo(formatInfo(blogCsdn, "<span class=\"count\">"), "</span>"));
                    put("reader", formatInfo(formatInfo(readerCsdn, "<span class=\"num\">"), "</span>"));
                    put("rank", formatInfo(formatInfo(rankCsdn, "<a class=\"grade-box-rankA\" href=\"https://blog.csdn.net/rank/writing_rank\\w*\" target=\"_blank\">\\s*"), "\\s*</a>"));
                    put("score", formatInfo(formatInfo(scoreCsdn, "<dd title=\""), "\">"));
                }});
            }});
        }};

        // Set cache
        redisService.setStr(INFO_KEY, JsonUtil.toStr(infoMap));
        return infoMap;
    }

    private String[] formatInfo(String[] strArr, String regex) {
        return strArr == null ? null : StrUtil.join(strArr, ", ").replaceAll(regex, "").split(", ");
    }
}
