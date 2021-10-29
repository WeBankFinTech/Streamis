package com.webank.wedatasphere.streamis.jobmanager.manager.entity;

import java.util.Date;

/**
 * @author limeng
 */
public class StreamJob {
    private Long id;
    private Long workspaceId;
    private Long projectId;
    private String name;
    private Integer jobType;
    private Long currentTaskId;
    private String currentVersion;
    private Date currentReleaseTime;
    private Integer status;
    private Long orgIdentification;
    private String createBy;
    private String label;
    private String currentReleased;
    private String description;
    private String submitUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Long getCurrentTaskId() {
        return currentTaskId;
    }

    public void setCurrentTaskId(Long currentTaskId) {
        this.currentTaskId = currentTaskId;
    }

    public String getCurrentVersion() {
        return currentVersion;
    }

    public void setCurrentVersion(String currentVersion) {
        this.currentVersion = currentVersion;
    }

    public Date getCurrentReleaseTime() {
        return currentReleaseTime;
    }

    public void setCurrentReleaseTime(Date currentReleaseTime) {
        this.currentReleaseTime = currentReleaseTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getOrgIdentification() {
        return orgIdentification;
    }

    public void setOrgIdentification(Long orgIdentification) {
        this.orgIdentification = orgIdentification;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getCurrentReleased() {
        return currentReleased;
    }

    public void setCurrentReleased(String currentReleased) {
        this.currentReleased = currentReleased;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public Long getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(Long workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getSubmitUser() {
        return submitUser;
    }

    public void setSubmitUser(String submitUser) {
        this.submitUser = submitUser;
    }

    public Integer getJobType() {
        return jobType;
    }

    public void setJobType(Integer jobType) {
        this.jobType = jobType;
    }
}
