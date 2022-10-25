package com.webank.wedatasphere.streamis.jobmanager.log.collector;

/**
 * Exception listener
 */
public interface ExceptionListener {

    /**
     * Listen the exception
     * @param subject the subject that throws the exception
     * @param t Throwable
     * @param message message
     */
    void onException(Object subject, Throwable t, String message);
}
