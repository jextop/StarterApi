package com.starter.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author Ding
 * @since 2020-01-28
 */
public class Auth implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    private String appKey;

    private String secret;

    private Boolean isDeleted;

    private String ip;

    private Long authId;

    private LocalDateTime created;

    private Long createdBy;

    private LocalDateTime updated;

    private Long updatedBy;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getAppKey() {
        return appKey;
    }

    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }
    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
    public Boolean getDeleted() {
        return isDeleted;
    }

    public void setDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public Long getAuthId() {
        return authId;
    }

    public void setAuthId(Long authId) {
        this.authId = authId;
    }
    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }
    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }
    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return "Auth{" +
            "name=" + name +
            ", description=" + description +
            ", appKey=" + appKey +
            ", secret=" + secret +
            ", isDeleted=" + isDeleted +
            ", ip=" + ip +
            ", authId=" + authId +
            ", created=" + created +
            ", createdBy=" + createdBy +
            ", updated=" + updated +
            ", updatedBy=" + updatedBy +
        "}";
    }
}
