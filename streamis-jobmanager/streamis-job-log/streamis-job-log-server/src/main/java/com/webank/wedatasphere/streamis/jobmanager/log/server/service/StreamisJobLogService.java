package com.webank.wedatasphere.streamis.jobmanager.log.server.service;

import com.webank.wedatasphere.streamis.jobmanager.log.entities.StreamisLogEvents;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJob;

/**
 * Job log service
 */
public interface StreamisJobLogService {

    /**
     * Store log events
     * @param user user own
     * @param events events
     */
    void store(String user, StreamisLogEvents events,String productName);

    Long getCurrentJobId(String projectName,String jobName);

    String getProductName(Long jobId,String value);
}
