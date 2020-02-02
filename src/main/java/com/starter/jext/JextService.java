package com.starter.jext;

import com.common.util.JsonUtil;
import com.common.util.StrUtil;
import com.starter.http.HttpService;
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
    RedisService redisService;

    @Autowired
    HttpService httpService;

    public Map<String, Object> getInfo(boolean forceUpdateCache) {
        Map<String, Object> infoMap = forceUpdateCache ? null : JsonUtil.parseObj(redisService.getStr(INFO_KEY));
        if (!MapUtils.isEmpty(infoMap)) {
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
        String[] blogCsdn = StrUtil.parse(strCsdnBlog, "<dl class=\"text-center\" title=\"[1-9]\\d*\">");
        String[] readerCsdn = StrUtil.parse(strCsdnBlog, "<span class=\"num\">[1-9]\\d*</span>");
        String[] rankCsdn = StrUtil.parse(strCsdnBlog, "<dl title=\"[1-9]\\d*\">");
        String[] scoreCsdn = StrUtil.parse(strCsdnBlog, "<dd title=\"[1-9]\\d*\">");

        infoMap = new HashMap<String, Object>() {{
            put("cto51", new HashMap<Object, Object>() {{
                put("course", new HashMap<Object, Object>() {{
                    put("count", parseNum(course51Cto));
                    put("userCount", parseNum(user51Cto));
                    put("user", parseNum(userDetail51Cto));
                }});
                put("blog", new HashMap<Object, Object>() {{
                    put("count", parseNum(blog51Cto, "[1-9]\\d*(W\\+)*"));
                    put("reader", parseNum(reader51Cto, "[1-9]\\d*(W\\+)*"));
                }});
            }});
            put("csdn", new HashMap<Object, Object>() {{
                put("course", new HashMap<Object, Object>() {{
                    put("userCount", parseNum(userCsdn));
                    put("user", parseNum(userDetailCsdn));
                }});
                put("blog", new HashMap<Object, Object>() {{
                    put("count", parseNum(blogCsdn));
                    put("reader", parseNum(readerCsdn));
                    put("rank", parseNum(rankCsdn));
                    put("score", parseNum(scoreCsdn));
                }});
            }});
        }};

        // Set cache
        redisService.setStr1Hour(INFO_KEY, JsonUtil.toStr(infoMap));
        return infoMap;
    }

    private String[] parseNum(String[] strArr) {
        return parseNum(strArr, "[1-9]\\d*");
    }

    private String[] parseNum(String[] strArr, String regex) {
        return strArr == null ? null : StrUtil.parse(StrUtil.join(strArr, ", "), regex);
    }
}
