package com.webank.wedatasphere.streamis.jobmanager.log.entities;

import java.util.concurrent.TimeUnit;

/**
 * Element defined of log
 */
public interface LogElement {

    /**
     * Sequence id
     * @return seq id
     */
    int getSequenceId();

    /**
     * Log time
     * @param unit unit
     * @return log time
     */
    long getLogTime(TimeUnit unit);

    /**
     * Get content
     * @return content array
     */
    String[] getContents();

}
