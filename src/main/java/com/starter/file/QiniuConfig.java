package com.starter.file;

import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("qiniu")
@ConditionalOnProperty(prefix = "qiniu", name = "secretKey")
public class QiniuConfig {
    int region;
    String accessKey;
    String secretKey;
    String bucket;
    String url;

    @Bean
    public UploadManager uploadManager() {
        return new UploadManager(configuration());
    }

    @Bean
    public BucketManager bucketManager() {
        return new BucketManager(auth(), configuration());
    }

    @Bean
    public Auth auth() {
        return Auth.create(accessKey, secretKey);
    }

    @Bean
    public com.qiniu.storage.Configuration configuration() {
        switch (region) {
            case 0:
                return new com.qiniu.storage.Configuration(Region.region0());
            default:
                return new com.qiniu.storage.Configuration(Region.autoRegion());
        }
    }

    public int getRegion() {
        return region;
    }

    public void setRegion(int region) {
        this.region = region;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
