package com.webank.wedatasphere.streamis.jobmanager.manager.entity;

public class StreamBml {
    private Long id;
    private String name;
    private Integer bmlType;
    private String orgIdentification;
    private String latestVersion;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBmlType() {
        return bmlType;
    }

    public void setBmlType(Integer bmlType) {
        this.bmlType = bmlType;
    }

    public String getOrgIdentification() {
        return orgIdentification;
    }

    public void setOrgIdentification(String orgIdentification) {
        this.orgIdentification = orgIdentification;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public void setLatestVersion(String latestVersion) {
        this.latestVersion = latestVersion;
    }
}
