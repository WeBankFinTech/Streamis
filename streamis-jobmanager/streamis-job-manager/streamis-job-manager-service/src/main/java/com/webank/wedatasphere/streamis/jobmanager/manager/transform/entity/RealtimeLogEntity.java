package com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity;

import java.util.List;

public class RealtimeLogEntity {
    private String logPath;

    private List<String>  logs;

    private Long endLine;


    public String getLogPath() {
        return logPath;
    }

    public void setLogPath(String logPath) {
        this.logPath = logPath;
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }

    public Long getEndLine() {
        return endLine;
    }

    public void setEndLine(Long endLine) {
        this.endLine = endLine;
    }
}
