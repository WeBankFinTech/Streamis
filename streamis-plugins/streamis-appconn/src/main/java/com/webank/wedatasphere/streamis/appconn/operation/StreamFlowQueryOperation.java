package com.webank.wedatasphere.streamis.appconn.operation;

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.query.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.rpc.Sender;
import com.webank.wedatasphere.streamis.appconn.ref.StreamFlowQueryRequestRef;
import com.webank.wedatasphere.streamis.appconn.ref.StreamFlowQueryResponseRef;
import com.webank.wedatasphere.streamis.plugins.common.StreamisPluginUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by yangzhiyue on 2021/4/23
 * Description:
 */
public class StreamFlowQueryOperation implements RefQueryOperation<StreamFlowQueryRequestRef, StreamFlowQueryResponseRef> {


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamFlowQueryOperation.class);

    private DevelopmentService developmentService;

    private Sender sender;

    private AppInstance appInstance;

    public StreamFlowQueryOperation(AppInstance appInstance){
        this.appInstance = appInstance;
        init();
    }

    private void init(){
        this.sender = Sender.getSender(StreamisPluginUtils.STREAMIS_RPC_SERVER_NAME + "-" + StreamisPluginUtils.getLabel(appInstance));
    }




    @Override
    public StreamFlowQueryResponseRef query(StreamFlowQueryRequestRef streamFlowQueryRequestRef) throws ExternalOperationFailedException {
        return null;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }
}
