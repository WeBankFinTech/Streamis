package com.webank.wedatasphere.streamis.appconn.operation;

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefImportOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.rpc.Sender;
import com.webank.wedatasphere.streamis.appconn.exception.StreamisExternalException;
import com.webank.wedatasphere.streamis.appconn.ref.StreamFlowImportRequestRef;
import com.webank.wedatasphere.streamis.appconn.ref.StreamFlowImportResponseRef;
import com.webank.wedatasphere.streamis.plugins.common.StreamisPluginUtils;
import com.webank.wedatasphere.streamis.workflow.common.protocol.StreamFlowImportRequest;
import com.webank.wedatasphere.streamis.workflow.common.protocol.StreamFlowImportResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * created by yangzhiyue on 2021/4/26
 * Description:
 */
public class StreamFlowImportOperation implements RefImportOperation<StreamFlowImportRequestRef, StreamFlowImportResponseRef> {


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamFlowImportOperation.class);

    private AppInstance appInstance;

    private Sender sender;

    private DevelopmentService developmentService;

    public StreamFlowImportOperation(AppInstance appInstance) {
        this.appInstance = appInstance;
        init();
    }

    private void init() {
        LOGGER.info("init stream flow import operation");
        sender = Sender.getSender(StreamisPluginUtils.STREAMIS_RPC_SERVER_NAME + "-" + StreamisPluginUtils.getLabel(appInstance));
    }


    @Override
    public StreamFlowImportResponseRef importRef(StreamFlowImportRequestRef streamFlowImportRequestRef) throws ExternalOperationFailedException {
        LOGGER.info("begin to import stream flow to streamis, bmlResourceId is {}, version is {}", streamFlowImportRequestRef.getResourceId(),streamFlowImportRequestRef.getBmlVersion());
        try{
            StreamFlowImportRequest streamFlowImportRequest = new StreamFlowImportRequest(streamFlowImportRequestRef.getName(),
                    streamFlowImportRequestRef.getUserName(),
                    streamFlowImportRequestRef.getProjectName(),
                    streamFlowImportRequestRef.getProjectId(),
                    streamFlowImportRequestRef.getResourceId(),
                    streamFlowImportRequestRef.getBmlVersion(),
                    streamFlowImportRequestRef.getOrcVersion(),
                    streamFlowImportRequestRef.getWorkspaceName(),
                    streamFlowImportRequestRef.getWorkspaceName());
            StreamFlowImportResponse streamFlowImportResponse = (StreamFlowImportResponse) sender.ask(streamFlowImportRequest);
            int status = streamFlowImportResponse.status();
            String errorMsg = streamFlowImportResponse.errorMessage();
            List<Long> flowIds = streamFlowImportResponse.flowIds().stream().map(o -> (Long)o).collect(Collectors.toList());
            LOGGER.info("end to import stream flow to streamis status is {}, errorMsg is {} response is {}", status, errorMsg, flowIds);
            if(status == 0){
                LOGGER.info("status is 0, it means success");
                return new StreamFlowImportResponseRef(flowIds);
            } else {
                LOGGER.info("status is {}, erroMsg is {}", status, errorMsg);
                throw new StreamisExternalException(600503, errorMsg);
            }
        }catch(StreamisExternalException exception){
            throw exception;
        } catch(final Throwable throwable){
            LOGGER.error("failed to import stream flow, errorMsg is {}", throwable.getMessage());
            throw new StreamisExternalException(600501, "failed to import stream flow", throwable);
        }
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }
}
