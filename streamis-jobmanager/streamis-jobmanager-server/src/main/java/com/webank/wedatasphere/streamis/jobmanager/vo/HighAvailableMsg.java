package com.webank.wedatasphere.streamis.jobmanager.vo;

public class HighAvailableMsg {

    String clusterName;

    String nodeIp;

    Boolean whetherManager;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getNodeIp() {
        return nodeIp;
    }

    public void setNodeIp(String nodeIp) {
        this.nodeIp = nodeIp;
    }

    public Boolean getWhetherManager() {
        return whetherManager;
    }
    public void setWhetherManager(Boolean whetherManager) {
        this.whetherManager = whetherManager;
    }
}
