package com.webank.wedatasphere.streamis.appconn.operation;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.crud.RefCreationOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.rpc.Sender;
import com.webank.wedatasphere.streamis.appconn.ref.StreamFlowCreateRequestRef;
import com.webank.wedatasphere.streamis.appconn.ref.StreamFlowCreateResponseRef;
import com.webank.wedatasphere.streamis.appconn.utils.StreamisUtils;
import com.webank.wedatasphere.streamis.plugins.common.StreamisPluginUtils;
import com.webank.wedatasphere.streamis.workflow.common.protocol.StreamFlowCreateRequest;
import com.webank.wedatasphere.streamis.workflow.common.protocol.StreamFlowCreateResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * created by yangzhiyue on 2021/4/20
 * Description:用于创建流式工作流的operation
 * 先可以使用rpc方式进行解决
 */
public class StreamFlowCreationOperation implements RefCreationOperation<StreamFlowCreateRequestRef, StreamFlowCreateResponseRef> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamFlowCreationOperation.class);


    private AppInstance appInstance;

    private Sender sender;

    private DevelopmentService developmentService;


    public StreamFlowCreationOperation(AppInstance appInstance){
        this.appInstance = appInstance;
    }

    private void init(){
        String envLabel = StreamisPluginUtils.getLabel(this.appInstance);
        String serverRealName = StreamisPluginUtils.STREAMIS_RPC_SERVER_NAME + "-" + envLabel;
        this.sender = Sender.getSender(serverRealName);
    }






    @Override
    public StreamFlowCreateResponseRef createRef(StreamFlowCreateRequestRef streamFlowCreateRequestRef) throws ExternalOperationFailedException {
        LOGGER.info("begin to create stream flow for user {}, ref is {}", streamFlowCreateRequestRef.getUserName(),
                StreamisUtils.COMMON_GSON.toJson(streamFlowCreateRequestRef));
        //todo linkedAppConnName先写死吧
        List<String> linkedAppConnNames = Lists.newArrayList("source","sink");
        StreamFlowCreateRequest streamFlowCreateRequest = new StreamFlowCreateRequest(streamFlowCreateRequestRef.getDSSOrchestratorInfo().getName(),
                streamFlowCreateRequestRef.getUserName(),
                streamFlowCreateRequestRef.getDSSOrchestratorInfo().getDesc(),
                streamFlowCreateRequestRef.getWorkspaceName(),
                streamFlowCreateRequestRef.getParentFlowId(),
                streamFlowCreateRequestRef.getDSSOrchestratorInfo().getUses(),
                linkedAppConnNames,
                streamFlowCreateRequestRef.getContextIDStr(),
                streamFlowCreateRequestRef.getProjectId(),
                streamFlowCreateRequestRef.getProjectName());
        StreamFlowCreateResponse response = null;
        try{
            response = (StreamFlowCreateResponse) sender.ask(streamFlowCreateRequest);
            LOGGER.info("end to create stream flow in streamis, flowId is {}", response.flowId());
        }catch(final Throwable throwable){
            LOGGER.error("failed to create stream flow");
            DSSExceptionUtils.dealErrorException(300603, "failed to create streamflow in streamis", throwable, ExternalOperationFailedException.class);
        }
        return new StreamFlowCreateResponseRef(response.flowId());
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }
}
