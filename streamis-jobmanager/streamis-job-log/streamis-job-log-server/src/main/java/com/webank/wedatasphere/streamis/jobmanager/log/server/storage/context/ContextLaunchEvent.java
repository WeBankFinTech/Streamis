package com.webank.wedatasphere.streamis.jobmanager.log.server.storage.context;

import java.util.List;

/**
 * Means that the storage context has been launched
 */
public class ContextLaunchEvent implements JobLogStorageContextListener.ContextEvent {

    /**
     * Storage contexts
     */
    private final List<JobLogStorageContext> contexts;

    public ContextLaunchEvent(List<JobLogStorageContext> contexts){
        this.contexts = contexts;
    }

    public List<JobLogStorageContext> getContextList() {
        return contexts;
    }

}
