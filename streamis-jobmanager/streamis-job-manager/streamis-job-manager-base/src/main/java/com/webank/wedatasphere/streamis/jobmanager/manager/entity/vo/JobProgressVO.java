package com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo;

public class JobProgressVO {
    private Long taskId;
    private Integer progress;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }
}
