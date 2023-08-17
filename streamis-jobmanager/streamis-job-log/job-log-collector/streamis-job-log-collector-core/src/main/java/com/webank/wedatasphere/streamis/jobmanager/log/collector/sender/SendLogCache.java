package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.cache.LogCache;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.buf.SendBuffer;
import com.webank.wedatasphere.streamis.jobmanager.log.entities.LogElement;

/**
 * Send log cache
 * @param <E>
 */
public interface SendLogCache<E extends LogElement> extends LogCache<E> {

    /**
     * Drain the logs into send buffer
     * @param sendBuffer send buffer
     * @param maxElements max element size
     * @return count
     */
    int drainLogsTo(SendBuffer<E> sendBuffer, int maxElements);
}
