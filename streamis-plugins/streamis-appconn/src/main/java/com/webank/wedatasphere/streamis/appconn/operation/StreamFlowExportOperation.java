package com.webank.wedatasphere.streamis.appconn.operation;

import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefExportOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.rpc.Sender;
import com.webank.wedatasphere.streamis.appconn.ref.StreamFlowExportRequestRef;
import com.webank.wedatasphere.streamis.appconn.ref.StreamFlowExportResponseRef;
import com.webank.wedatasphere.streamis.plugins.common.StreamisPluginUtils;
import com.webank.wedatasphere.streamis.workflow.common.protocol.StreamFlowExportRequest;
import com.webank.wedatasphere.streamis.workflow.common.protocol.StreamFlowExportResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by yangzhiyue on 2021/4/21
 * Description:
 * 还是先走rpc方式
 */
public class StreamFlowExportOperation implements RefExportOperation<StreamFlowExportRequestRef, StreamFlowExportResponseRef> {


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamFlowExportOperation.class);

    private AppInstance appInstance;

    private String streamisExportUrl;

    private static final String EXPORT_URL_SUFFIX = "api/rest_j/v1/streamis/exportStreamFlow";

    private Sender sender;

    private DevelopmentService developmentService;

    public StreamFlowExportOperation(AppInstance appInstance){
        this.appInstance = appInstance;
        init();
    }

    private void init() {
        LOGGER.info("to init stream flow export operation");
        sender = Sender.getSender(StreamisPluginUtils.STREAMIS_RPC_SERVER_NAME + "-" + StreamisPluginUtils.getLabel(appInstance));
    }


    @Override
    public StreamFlowExportResponseRef exportRef(StreamFlowExportRequestRef streamFlowExportRequestRef) throws ExternalOperationFailedException {
        LOGGER.info("begin to export stream flow {} from streamis", streamFlowExportRequestRef.getStreamFlowId());
        StreamFlowExportRequest streamFlowExportRequest = new StreamFlowExportRequest(streamFlowExportRequestRef.getUserName(),
                streamFlowExportRequestRef.getStreamFlowId(),
                streamFlowExportRequestRef.getProjectId(),
                streamFlowExportRequestRef.getProjectName(),
                streamFlowExportRequestRef.getWorkspaceName());
        StreamFlowExportResponse streamFlowExportResponse = null;
        try{
            streamFlowExportResponse = (StreamFlowExportResponse) sender.ask(streamFlowExportRequest);
            LOGGER.info("end to export stream flow from streamis, response is {}", streamFlowExportResponse.bmlResourceId());
        }catch(final Throwable t){
            LOGGER.error("Fail to export stream flow from streamis");
            DSSExceptionUtils.dealErrorException(300605, "failed to export ref from streamis",t,ExternalOperationFailedException.class);
        }
        return new StreamFlowExportResponseRef(streamFlowExportResponse.bmlResourceId(), streamFlowExportResponse.bmlVersion());
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }
}
