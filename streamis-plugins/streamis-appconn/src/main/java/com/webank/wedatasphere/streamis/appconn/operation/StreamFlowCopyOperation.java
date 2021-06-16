package com.webank.wedatasphere.streamis.appconn.operation;

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.crud.RefCopyOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.streamis.appconn.ref.StreamFlowCopyRequestRef;
import com.webank.wedatasphere.streamis.appconn.ref.StreamFlowCopyResponseRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by yangzhiyue on 2021/4/23
 * Description:
 */
public class StreamFlowCopyOperation implements RefCopyOperation<StreamFlowCopyRequestRef, StreamFlowCopyResponseRef> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamFlowCopyOperation.class);


    @Override
    public StreamFlowCopyResponseRef copyRef(StreamFlowCopyRequestRef streamFlowCopyRequestRef) throws ExternalOperationFailedException {
        return null;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {

    }
}
