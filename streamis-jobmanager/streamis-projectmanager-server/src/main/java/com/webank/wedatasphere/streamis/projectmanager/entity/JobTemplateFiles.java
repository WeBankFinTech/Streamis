package com.webank.wedatasphere.streamis.projectmanager.entity;

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamisFile;

import java.util.Date;

public class JobTemplateFiles {

    private Long id;

    private String storePath;

    private String name;

    private String metaJson;

    private String version;

    private String projectName;

    private Date date;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStorePath() {
        return storePath;
    }

    public void setStorePath(String storePath) {
        this.storePath = storePath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMetaJson() {
        return metaJson;
    }

    public void setMetaJson(String metaJson) {
        this.metaJson = metaJson;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
