package com.starter.entity;

import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author Ding
 * @since 2020-02-15
 */
public class Voucher implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;

    private Integer status;

    private Integer ops;

    private String taskId;

    private Integer processCount;

    private Integer queryCount;

    private Boolean isFailed;

    private String failReason;

    private String extraInfo;

    private LocalDateTime timeWaiting;

    private LocalDateTime timePreparing;

    private LocalDateTime timeProcessing;

    private LocalDateTime timeFinished;

    private String urlCallback;

    private Boolean isCallback;

    private String callbackRet;

    private LocalDateTime timeCallback;

    private Integer callbackCount;

    private Boolean isDeleted;

    private String ip;

    private Long authId;

    private LocalDateTime created;

    private Long createdBy;

    private LocalDateTime updated;

    private Long updatedBy;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    public Integer getOps() {
        return ops;
    }

    public void setOps(Integer ops) {
        this.ops = ops;
    }
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    public Integer getProcessCount() {
        return processCount;
    }

    public void setProcessCount(Integer processCount) {
        this.processCount = processCount;
    }
    public Integer getQueryCount() {
        return queryCount;
    }

    public void setQueryCount(Integer queryCount) {
        this.queryCount = queryCount;
    }
    public Boolean getFailed() {
        return isFailed;
    }

    public void setFailed(Boolean isFailed) {
        this.isFailed = isFailed;
    }
    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }
    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }
    public LocalDateTime getTimeWaiting() {
        return timeWaiting;
    }

    public void setTimeWaiting(LocalDateTime timeWaiting) {
        this.timeWaiting = timeWaiting;
    }
    public LocalDateTime getTimePreparing() {
        return timePreparing;
    }

    public void setTimePreparing(LocalDateTime timePreparing) {
        this.timePreparing = timePreparing;
    }
    public LocalDateTime getTimeProcessing() {
        return timeProcessing;
    }

    public void setTimeProcessing(LocalDateTime timeProcessing) {
        this.timeProcessing = timeProcessing;
    }
    public LocalDateTime getTimeFinished() {
        return timeFinished;
    }

    public void setTimeFinished(LocalDateTime timeFinished) {
        this.timeFinished = timeFinished;
    }
    public String getUrlCallback() {
        return urlCallback;
    }

    public void setUrlCallback(String urlCallback) {
        this.urlCallback = urlCallback;
    }
    public Boolean getCallback() {
        return isCallback;
    }

    public void setCallback(Boolean isCallback) {
        this.isCallback = isCallback;
    }
    public String getCallbackRet() {
        return callbackRet;
    }

    public void setCallbackRet(String callbackRet) {
        this.callbackRet = callbackRet;
    }
    public LocalDateTime getTimeCallback() {
        return timeCallback;
    }

    public void setTimeCallback(LocalDateTime timeCallback) {
        this.timeCallback = timeCallback;
    }
    public Integer getCallbackCount() {
        return callbackCount;
    }

    public void setCallbackCount(Integer callbackCount) {
        this.callbackCount = callbackCount;
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
        return "Voucher{" +
            "code=" + code +
            ", status=" + status +
            ", ops=" + ops +
            ", taskId=" + taskId +
            ", processCount=" + processCount +
            ", queryCount=" + queryCount +
            ", isFailed=" + isFailed +
            ", failReason=" + failReason +
            ", extraInfo=" + extraInfo +
            ", timeWaiting=" + timeWaiting +
            ", timePreparing=" + timePreparing +
            ", timeProcessing=" + timeProcessing +
            ", timeFinished=" + timeFinished +
            ", urlCallback=" + urlCallback +
            ", isCallback=" + isCallback +
            ", callbackRet=" + callbackRet +
            ", timeCallback=" + timeCallback +
            ", callbackCount=" + callbackCount +
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
