package com.webank.wedatasphere.streamis.jobmanager.manager.entity;

/**
 * @author limeng
 */
public class StreamBmlVersion {
    private Long id;
    private Long bmlId;
    private String version;
    private String storagePath;
    private String attribute;
    private String resourceId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBmlId() {
        return bmlId;
    }

    public void setBmlId(Long bmlId) {
        this.bmlId = bmlId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

}
