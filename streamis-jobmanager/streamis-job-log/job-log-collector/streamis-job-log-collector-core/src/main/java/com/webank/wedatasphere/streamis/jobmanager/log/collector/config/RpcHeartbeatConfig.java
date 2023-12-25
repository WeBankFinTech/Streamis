package com.webank.wedatasphere.streamis.jobmanager.log.collector.config;

public class RpcHeartbeatConfig {

    /**
     * send heartbeat address
     */
    private String heartbeatAddress;

    /**
     * Heartbeat interval
     */

    private int heartbeatInterval = 30 * 60 * 1000;


    public RpcHeartbeatConfig() {
    }

    public RpcHeartbeatConfig(String heartbeatAddress, int heartbeatInterval) {
        this.heartbeatAddress = heartbeatAddress;
        this.heartbeatInterval = heartbeatInterval;
    }

    public String getHeartbeatAddress() {
        return heartbeatAddress;
    }

    public void setHeartbeatAddress(String heartbeatAddress) {
        this.heartbeatAddress = heartbeatAddress;
    }

    public int getHeartbeatInterval() {
        return heartbeatInterval;
    }

    public void setHeartbeatInterval(int heartbeatInterval) {
        this.heartbeatInterval = heartbeatInterval;
    }
}
