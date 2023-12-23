package com.webank.wedatasphere.streamis.jobmanager.log.server.service;

import com.webank.wedatasphere.streamis.jobmanager.log.entities.StreamisHeartbeat;
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.StreamRegisterMapper;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamRegister;
import org.springframework.beans.factory.annotation.Autowired;

public interface StreamisRegisterService {

    void addStreamRegister(StreamRegister streamRegister);

    void updateHeartbeatTime(StreamRegister streamRegister);

    void updateRegisterTime(StreamRegister streamRegister);

    StreamRegister getInfoByApplicationName(String applicationName);

}
