package com.starter.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * @author ding
 */
@Configuration
@ConfigurationProperties("spring.servlet.multipart")
public class MultipartConfig {
    private String location;
    private String abstractLocation;

    public String getLocation() {
        if (abstractLocation == null) {
            synchronized (MultipartConfig.class) {
                if (abstractLocation == null) {
                    File file = new File(location);
                    abstractLocation = file.getAbsolutePath();
                }
            }
        }
        return abstractLocation;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
