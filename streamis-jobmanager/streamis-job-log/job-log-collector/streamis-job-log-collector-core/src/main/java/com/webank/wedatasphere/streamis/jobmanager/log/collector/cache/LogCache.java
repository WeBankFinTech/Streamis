package com.webank.wedatasphere.streamis.jobmanager.log.collector.cache;

import com.webank.wedatasphere.streamis.jobmanager.log.entities.LogElement;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Log cache
 * @param <E> element
 */
public interface LogCache<E extends LogElement> {

    /**
     * Cache log
     * @param logElement log element
     */
    void cacheLog(E logElement) throws InterruptedException;

    /**
     * Drain log elements into collection
     * @param elements elements
     * @param maxElements max elements size
     * @return count
     */
    int drainLogsTo(List<E> elements, int maxElements);

    /**
     * Take log element
     * @return log element
     */
    E takeLog(long timeout, TimeUnit unit) throws InterruptedException;

    /**
     * If the cache is full
     * @return
     */
    boolean isCacheable();
    /**
     * Release the resource
     */
    void destroy();
}
