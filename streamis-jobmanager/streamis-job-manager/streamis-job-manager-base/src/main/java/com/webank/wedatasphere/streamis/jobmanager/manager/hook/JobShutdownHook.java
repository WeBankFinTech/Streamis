package com.webank.wedatasphere.streamis.jobmanager.manager.hook;

public interface JobShutdownHook {

    String getName();

    void doBeforeJobShutdown();

    void doAfterJobShutdown();

}
