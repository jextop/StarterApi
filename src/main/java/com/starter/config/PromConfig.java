package com.starter.config;

import com.common.util.LogUtil;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PromConfig {
    @Bean
    MeterRegistryCustomizer<MeterRegistry> config(@Value("${management.metrics.tags.application}") String appName) {
        LogUtil.info("Prometheus config", appName);
        return (registry) -> registry.config().commonTags("application", appName);
    }
}
