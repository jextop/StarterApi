package com.starter.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author Ding
 * @since 2020-01-08
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String title;

    private String description;

    private String password;

    private Integer permission;

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
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public Integer getPermission() {
        return permission;
    }

    public void setPermission(Integer permission) {
        this.permission = permission;
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
        return "User{" +
            "name=" + name +
            ", title=" + title +
            ", description=" + description +
            ", password=" + password +
            ", permission=" + permission +
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
