package com.webank.wedatasphere.streamis.jobmanager.log.server.storage.context;

/**
 * Means that the storage context has been downed
 */
public class ContextDownEvent implements JobLogStorageContextListener.ContextEvent {

    /**
     * Context id
     */
    private final String contextId;

    public ContextDownEvent(String contextId){
        this.contextId = contextId;
    }

    public String getContextId() {
        return contextId;
    }
}
