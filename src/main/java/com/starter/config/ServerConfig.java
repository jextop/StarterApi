package com.starter.config;

import com.common.http.IpUtil;
import com.common.util.StrUtil;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("server")
public class ServerConfig {
    private String address;
    private int port;

    public String getServerUrl() {
        if (!StrUtil.isEmpty(address) && !address.equalsIgnoreCase("localhost")) {
            String strPort = port > 0 && port != 80 ? String.format(":%d", port) : "";
            return String.format("http://%s%s", address, strPort);
        }
        return IpUtil.getLocalUrl(port);
    }

    public void setAddress(String domain) {
        this.address = domain;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
