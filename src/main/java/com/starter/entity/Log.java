package com.starter.entity;

import java.io.Serializable;

/**
 * <p>
 * <p>
 * </p>
 *
 * @author Ding
 * @since 2020-01-07
 */
public class Log implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer ops;

    private String tableName;

    private String summary;

    private String ip;

    private Long authId;

    private Long created;

    private Long createdBy;

    public Integer getOps() {
        return ops;
    }

    public void setOps(Integer ops) {
        this.ops = ops;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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

    public Long getCreated() {
        return created;
    }

    public void setCreated(Long created) {
        this.created = created;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public String toString() {
        return "Log{" +
                "ops=" + ops +
                ", tableName=" + tableName +
                ", summary=" + summary +
                ", ip=" + ip +
                ", authId=" + authId +
                ", created=" + created +
                ", createdBy=" + createdBy +
                "}";
    }
}
