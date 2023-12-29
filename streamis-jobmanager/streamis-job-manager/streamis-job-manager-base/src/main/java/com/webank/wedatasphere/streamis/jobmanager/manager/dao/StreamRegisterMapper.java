package com.webank.wedatasphere.streamis.jobmanager.manager.dao;

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamRegister;

import java.util.List;

public interface StreamRegisterMapper {

    List<StreamRegister> getInfo ();

    StreamRegister getInfoByApplicationName(String applicationName);

    StreamRegister getInfoByJobId(Long jobId);

    void insert(StreamRegister streamRegister);

    void delete(String applicationName);

    void updateHeartbeatTime(StreamRegister streamRegister);

    void updateRegisterTime(StreamRegister streamRegister);
}
