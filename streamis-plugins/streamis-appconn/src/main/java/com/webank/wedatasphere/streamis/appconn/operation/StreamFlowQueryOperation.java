package com.webank.wedatasphere.streamis.appconn.operation;

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.query.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.streamis.appconn.ref.StreamFlowQueryRequestRef;
import com.webank.wedatasphere.streamis.appconn.ref.StreamFlowQueryResponseRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by yangzhiyue on 2021/4/23
 * Description:
 */
public class StreamFlowQueryOperation implements RefQueryOperation<StreamFlowQueryRequestRef, StreamFlowQueryResponseRef> {


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamFlowQueryOperation.class);




    @Override
    public StreamFlowQueryResponseRef query(StreamFlowQueryRequestRef streamFlowQueryRequestRef) throws ExternalOperationFailedException {
        return null;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {

    }
}
