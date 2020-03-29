package com.starter.entity;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author Ding
 * @since 2020-02-15
 */
public class File implements Serializable {

    private static final long serialVersionUID = 1L;

    private String code;

    private String url;

    private String name;

    private String md5;

    private Long size;

    private Integer downloadCount;

    private Integer fileType;

    private Integer location;

    private Boolean isDeleted;

    private String ip;

    private Long authId;

    private Long createdBy;

    private Long updatedBy;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }
    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
    public Integer getDownloadCount() {
        return downloadCount;
    }

    public void setDownloadCount(Integer downloadCount) {
        this.downloadCount = downloadCount;
    }
    public Integer getFileType() {
        return fileType;
    }

    public void setFileType(Integer fileType) {
        this.fileType = fileType;
    }
    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
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
    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }
    public Long getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(Long updatedBy) {
        this.updatedBy = updatedBy;
    }

    @Override
    public String toString() {
        return "File{" +
            "code=" + code +
            ", url=" + url +
            ", name=" + name +
            ", md5=" + md5 +
            ", size=" + size +
            ", downloadCount=" + downloadCount +
            ", fileType=" + fileType +
            ", location=" + location +
            ", isDeleted=" + isDeleted +
            ", ip=" + ip +
            ", authId=" + authId +
            ", createdBy=" + createdBy +
            ", updatedBy=" + updatedBy +
        "}";
    }
}
