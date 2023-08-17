package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.ExceptionListener;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.cache.LogCache;
import com.webank.wedatasphere.streamis.jobmanager.log.entities.LogElement;

/**
 * Rpc Log sender
 */
public interface RpcLogSender<T extends LogElement>  {

    /**
     * Produce log cache
     * @return log cache
     */
    LogCache<T> getOrCreateLogCache();

    /**
     * Send log (async)
     * @param log log element
     */
    void sendLog(T log);

    /**
     * Send log (sync)
     * @param log log element
     */
    void syncSendLog(T log);

    /**
     * Exception listener
     * @param listener listener
     */
    void setExceptionListener(ExceptionListener listener);
    /**
     * Close sender
     */
    void close();
}
