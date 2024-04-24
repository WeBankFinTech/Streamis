package com.webank.wedatasphere.streamis.jobmanager.manager.hook;

import java.util.Map;

public interface StreamisJobShutdownHook {

    String getName();

    void doBeforeJobShutdown(String taskId, String projectName, String jobName, long timeoutMills, Map<String, Object> params);

    void doAfterJobShutdown(String taskId, String projectName, String jobName, long timeoutMills, Map<String, Object> params);

}
