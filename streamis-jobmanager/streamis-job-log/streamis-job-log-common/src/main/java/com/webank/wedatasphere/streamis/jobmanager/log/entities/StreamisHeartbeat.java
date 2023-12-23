package com.webank.wedatasphere.streamis.jobmanager.log.entities;

public class StreamisHeartbeat {

    private String applicationName;

    private String passwordOrHeartbeat;

    private String sign;

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public StreamisHeartbeat() {
    }

    public StreamisHeartbeat(String applicationName, String passwordOrHeartbeat, String sign) {
        this.applicationName = applicationName;
        this.passwordOrHeartbeat = passwordOrHeartbeat;
        this.sign = sign;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getPasswordOrHeartbeat() {
        return passwordOrHeartbeat;
    }

    public void setPasswordOrHeartbeat(String passwordOrHeartbeat) {
        this.passwordOrHeartbeat = passwordOrHeartbeat;
    }
}
