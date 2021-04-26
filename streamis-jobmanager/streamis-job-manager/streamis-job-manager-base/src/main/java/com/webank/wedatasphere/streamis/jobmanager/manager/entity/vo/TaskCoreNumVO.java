package com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo;

/**
 * job核心指标
 */
public class TaskCoreNumVO {
    private Long projectId;
    //失败任务数目
    private Integer failureNum;
    //运行数目
    private Integer runningNum;
    //慢任务数目
    private Integer slowTaskNum;
    //告警任务
    private Integer alertNum;
    //等待重启数目
    private Integer waitRestartNum;
    //已完成数目
    private Integer successNum;


    public Integer getFailureNum() {
        return failureNum;
    }

    public void setFailureNum(Integer failureNum) {
        this.failureNum = failureNum;
    }

    public Integer getRunningNum() {
        return runningNum;
    }

    public void setRunningNum(Integer runningNum) {
        this.runningNum = runningNum;
    }

    public Integer getSlowTaskNum() {
        return slowTaskNum;
    }

    public void setSlowTaskNum(Integer slowTaskNum) {
        this.slowTaskNum = slowTaskNum;
    }

    public Integer getAlertNum() {
        return alertNum;
    }

    public void setAlertNum(Integer alertNum) {
        this.alertNum = alertNum;
    }

    public Integer getWaitRestartNum() {
        return waitRestartNum;
    }

    public void setWaitRestartNum(Integer waitRestartNum) {
        this.waitRestartNum = waitRestartNum;
    }

    public Integer getSuccessNum() {
        return successNum;
    }

    public void setSuccessNum(Integer successNum) {
        this.successNum = successNum;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }
}
