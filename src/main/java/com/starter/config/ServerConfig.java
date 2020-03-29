package com.starter.config;

import com.common.http.IpUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author ding
 */
@Configuration
@ConfigurationProperties("server")
public class ServerConfig {
    private String address;
    private int port;

    public String getServerUrl() {
        if (StringUtils.isNotEmpty(address) && !address.equalsIgnoreCase("localhost")) {
            String strPort = port > 0 && port != 80 ? String.format(":%d", port) : "";
            return String.format("http://%s%s", address, strPort);
        }
        return IpUtil.getLocalUrl(port);
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
