package com.webank.wedatasphere.streamis.jobmanager.log.server.service.impl;

import com.webank.wedatasphere.streamis.jobmanager.log.entities.StreamisHeartbeat;
import com.webank.wedatasphere.streamis.jobmanager.log.server.service.StreamisRegisterService;
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.StreamJobMapper;
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.StreamRegisterMapper;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJob;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamRegister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class StreamisRegisterServiceImpl implements StreamisRegisterService {

    @Autowired
    private StreamRegisterMapper streamRegisterMapper;

    @Autowired
    private StreamJobMapper streamJobMapper;

    public void addStreamRegister(StreamRegister streamRegister) {
        streamRegisterMapper.insert(streamRegister);
    }

    @Override
    public void updateHeartbeatTime(StreamRegister streamRegister) {
        streamRegisterMapper.updateHeartbeatTime(streamRegister);
    }

    @Override
    public void updateRegisterTime(StreamRegister streamRegister) {
        streamRegisterMapper.updateRegisterTime(streamRegister);
    }

    @Override
    public StreamRegister getInfoByApplicationName(String applicationName) {
        return streamRegisterMapper.getInfoByApplicationName(applicationName);
    }

    @Override
    public StreamJob getJobByApplicationName(String applicationName) {
        return streamJobMapper.getJobByProjectAndJobName(applicationName);
    }

    @Override
    public void deleteRegister(String applicationName) {
        streamRegisterMapper.delete(applicationName);
    }

}
