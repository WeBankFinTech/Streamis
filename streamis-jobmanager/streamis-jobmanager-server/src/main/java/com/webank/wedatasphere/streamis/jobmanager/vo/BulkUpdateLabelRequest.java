package com.webank.wedatasphere.streamis.jobmanager.vo;

import java.util.List;

public class BulkUpdateLabelRequest {
    private List<BulkUpdateLabel>  tasks;

    public List<BulkUpdateLabel> getTasks() {
        return tasks;
    }

    public void setTasks(List<BulkUpdateLabel> tasks) {
        this.tasks = tasks;
    }
}
