package com.webank.wedatasphere.streamis.jobmanager.manager.entity;

import java.util.Date;

/**
 * @author limeng
 */
public class StreamTask {
    private Long id;
    private Long jobVersionId;
    private String jobName;
    private String submitUser;
    private String version;
    private Date startTime;
    private Date endTime;
    private String errDesc;
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getErrDesc() {
        return errDesc;
    }

    public void setErrDesc(String errDesc) {
        this.errDesc = errDesc;
    }

    public Long getJobVersionId() {
        return jobVersionId;
    }

    public void setJobVersionId(Long jobVersionId) {
        this.jobVersionId = jobVersionId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getSubmitUser() {
        return submitUser;
    }

    public void setSubmitUser(String submitUser) {
        this.submitUser = submitUser;
    }
}
