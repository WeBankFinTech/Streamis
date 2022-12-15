package com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket;

import com.webank.wedatasphere.streamis.jobmanager.log.entities.LogElement;

/**
 * Storage writer for job log
 */
public interface JobLogStorageWriter {

    /**
     * Write log element
     * @param logEl elements
     * @param <T>
     */
    <T extends LogElement> void write(LogElement logEl);

    /**
     * Write log line
     * @param logLine log line
     */
    void write(String logLine);

    /**
     * Close log storage
     */
    void close();
}
