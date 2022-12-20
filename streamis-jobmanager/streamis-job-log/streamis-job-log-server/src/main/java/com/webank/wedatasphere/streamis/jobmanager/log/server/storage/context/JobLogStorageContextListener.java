package com.webank.wedatasphere.streamis.jobmanager.log.server.storage.context;

/**
 * Context listener
 */
public interface JobLogStorageContextListener {

    /**
     * Listen the context event
     * @param event event
     */
    void onContextEvent(ContextEvent event);

    interface ContextEvent{

    }
}
