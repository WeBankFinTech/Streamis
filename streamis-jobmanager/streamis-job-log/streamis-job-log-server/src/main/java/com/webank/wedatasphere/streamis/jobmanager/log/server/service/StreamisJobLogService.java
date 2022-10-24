package com.webank.wedatasphere.streamis.jobmanager.log.server.service;

import com.webank.wedatasphere.streamis.jobmanager.log.entities.StreamisLogEvents;

/**
 * Job log service
 */
public interface StreamisJobLogService {

    /**
     * Store log events
     * @param user user own
     * @param events events
     */
    void store(String user, StreamisLogEvents events);
}
