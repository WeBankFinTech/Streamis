package com.webank.wedatasphere.streamis.jobmanager.vo;

public class HighAvailableMsg {

    String clusterName;

    String clusterIp;

    Boolean whetherManager;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterIp() {
        return clusterIp;
    }

    public void setClusterIp(String clusterIp) {
        this.clusterIp = clusterIp;
    }

    public Boolean getWhetherManager() {
        return whetherManager;
    }
    public void setWhetherManager(Boolean whetherManager) {
        this.whetherManager = whetherManager;
    }
}
