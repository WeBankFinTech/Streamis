package com.webank.wedatasphere.streamis.workflow.server.service;

import com.webank.wedatasphere.streamis.workflow.server.entity.StreamisFlow;
import com.webank.wedatasphere.streamis.workflow.server.exception.StreamisErrorException;

/**
 * created by yangzhiyue on 2021/4/19
 * Description:
 */
public interface StreamFlowService {

    StreamisFlow createStreamFlow();

    void updateStreamFlow();

    void publishStreamFlow();

    void copyStreamFlow();

    void deleteStreamFlow() throws StreamisErrorException;

}
