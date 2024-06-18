package com.webank.wedatasphere.streamis.jobmanager.log.server.service;

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJob;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamRegister;

public interface StreamisRegisterService {

    void addStreamRegister(StreamRegister streamRegister);

    void updateHeartbeatTime(StreamRegister streamRegister);

    void updateRegisterTime(StreamRegister streamRegister);

    StreamRegister getInfoByApplicationName(String applicationName);

    StreamJob getJobByApplicationName(String applicationName);

    void deleteRegister(String applicationName);
}
