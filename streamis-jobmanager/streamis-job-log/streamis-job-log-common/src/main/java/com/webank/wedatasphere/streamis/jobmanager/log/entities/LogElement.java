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
     * @return log time
     */
    long getLogTimeStamp();

    /**
     * Get content
     * @return content array
     */
    String[] getContents();

    /**
     * The importance of log
     * 0: useless, 1: normal, 2:important
     * @return
     */
    int mark();
}
