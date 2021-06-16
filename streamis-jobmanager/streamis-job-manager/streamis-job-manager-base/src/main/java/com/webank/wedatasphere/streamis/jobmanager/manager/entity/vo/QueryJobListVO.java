package com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo;

public class QueryJobListVO {
    private Long jobId;
    private String jobName;
    private Integer taskStatus;
    private String lastReleaseTime;
    private String label;
    private String version;
    private String lastRelease;
    private String description;

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public Integer getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(Integer taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getLastReleaseTime() {
        return lastReleaseTime;
    }

    public void setLastReleaseTime(String lastReleaseTime) {
        this.lastReleaseTime = lastReleaseTime;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLastRelease() {
        return lastRelease;
    }

    public void setLastRelease(String lastRelease) {
        this.lastRelease = lastRelease;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
