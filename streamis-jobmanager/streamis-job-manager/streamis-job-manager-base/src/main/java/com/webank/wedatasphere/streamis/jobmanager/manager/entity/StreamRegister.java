package com.webank.wedatasphere.streamis.jobmanager.manager.entity;

import java.util.Date;

public class StreamRegister {

    private Long id;

    private Long jobId;

    private String applicationName;

    private String password;

    private Date registerTime;

    private Date heartbeatTime;


    public StreamRegister() {
    }

    public StreamRegister(Long id, Long jobId, String applicationName, String password, Date registerTime, Date heartbeatTime) {
        this.id = id;
        this.jobId = jobId;
        this.applicationName = applicationName;
        this.password = password;
        this.registerTime = registerTime;
        this.heartbeatTime = heartbeatTime;
    }



    public Date getHeartbeatTime() {
        return heartbeatTime;
    }

    public void setHeartbeatTime(Date heartbeatTime) {
        this.heartbeatTime = heartbeatTime;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }
}
