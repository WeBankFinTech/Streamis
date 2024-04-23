package com.webank.wedatasphere.streamis.jobmanager.manager.hook;

import java.util.Map;

public interface StreamisJobShutdowHook {

    String getName();

    void doBeforeJobShutdown(String jobId, String projectName, String jobName, int timeoutMills, Map<String, Object> params);

    void doAfterJobShutdown(String jobId, String projectName, String jobName, int timeoutMills, Map<String, Object> params);

}
