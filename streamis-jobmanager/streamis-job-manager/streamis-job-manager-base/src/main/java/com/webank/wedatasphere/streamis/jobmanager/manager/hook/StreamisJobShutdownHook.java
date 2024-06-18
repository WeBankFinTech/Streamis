package com.webank.wedatasphere.streamis.jobmanager.manager.hook;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

public interface StreamisJobShutdownHook {

    String getName();

    void doBeforeJobShutdown(String taskId, String projectName, String jobName, long timeoutMills, Map<String, Object> params) throws ExecutionException, TimeoutException, InterruptedException;

    void doAfterJobShutdown(String taskId, String projectName, String jobName, long timeoutMills, Map<String, Object> params) throws ExecutionException, TimeoutException, InterruptedException;

    void cancel() throws ExecutionException, TimeoutException, InterruptedException;
}
