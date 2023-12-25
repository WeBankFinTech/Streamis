package com.webank.wedatasphere.streamis.jobmanager.manager.entity;

import java.util.Date;

public class StreamRegister {

    private Long Id;

    private String applicationName;

    private String password;

    private Date registerTime;

    private Date heartbeatTime;


    public StreamRegister() {
    }

    public StreamRegister(Long id, String applicationName, String password, Date registerTime, Date heartbeatTime) {
        Id = id;
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
        return Id;
    }

    public void setId(Long id) {
        Id = id;
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
