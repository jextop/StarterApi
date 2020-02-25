package com.common.http;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ReqUtil {
    private static final String UNKNOWN = "unknown";

    public static String getIp(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String s = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(s) && !UNKNOWN.equalsIgnoreCase(s)) {
            // 多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = s.indexOf(",");
            return index < 0 ? s : s.substring(0, index);
        }

        s = request.getHeader("X-Real-IP");
        if (StringUtils.isBlank(s) || UNKNOWN.equalsIgnoreCase(s)) {
            s = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(s) || UNKNOWN.equalsIgnoreCase(s)) {
            s = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(s) || UNKNOWN.equalsIgnoreCase(s)) {
            s = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(s) || UNKNOWN.equalsIgnoreCase(s)) {
            s = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(s) || UNKNOWN.equalsIgnoreCase(s)) {
            s = request.getRemoteAddr();
        }

        // 获取本地ip
        if ("127.0.0.1".equals(s) || "0:0:0:0:0:0:0:1".equals(s)) {
            try {
                s = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                System.out.println(e.getMessage());
            }
        }
        return s;
    }
}
