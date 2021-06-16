package com.webank.wedatasphere.streamis.workflow.server.service.impl;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.common.entity.IOEnv;
import com.webank.wedatasphere.dss.common.exception.DSSErrorException;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.dss.workflow.WorkFlowManager;
import com.webank.wedatasphere.dss.workflow.common.entity.DSSFlow;
import com.webank.wedatasphere.dss.workflow.entity.DSSFlowImportParam;
import com.webank.wedatasphere.dss.workflow.service.DSSFlowService;
import com.webank.wedatasphere.streamis.workflow.common.protocol.StreamFlowCopyRequest;
import com.webank.wedatasphere.streamis.workflow.common.protocol.StreamFlowCreateRequest;
import com.webank.wedatasphere.streamis.workflow.common.protocol.StreamFlowExportRequest;
import com.webank.wedatasphere.streamis.workflow.common.protocol.StreamFlowImportRequest;
import com.webank.wedatasphere.streamis.workflow.server.entity.ExportResponse;
import com.webank.wedatasphere.streamis.workflow.server.exception.StreamisFlowErrorException;
import com.webank.wedatasphere.streamis.workflow.server.service.StreamFlowService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/19
 * Description:
 */
@Service
public class StreamFlowServiceImpl implements StreamFlowService {


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamFlowServiceImpl.class);


    @Autowired
    private WorkFlowManager workFlowManager;


    @Autowired
    DSSFlowService dssFlowService;

    @Override
    public DSSFlow createStreamFlow(StreamFlowCreateRequest streamFlowCreateRequest) throws StreamisFlowErrorException {
        try{
            LOGGER.info("begin to create stream flow {} for user {}", streamFlowCreateRequest.flowName(), streamFlowCreateRequest.createBy());
            //todo 以后可以通过orchestrator传过来，但是创建一般都是在dev
            List<DSSLabel> dssLabels = Lists.newArrayList(new DSSLabel("dev"));
            DSSFlow dssFlow = workFlowManager.createWorkflow(streamFlowCreateRequest.createBy(), streamFlowCreateRequest.flowName(),
                    streamFlowCreateRequest.contextId(), streamFlowCreateRequest.description(), streamFlowCreateRequest.parentFlowId(),
                    streamFlowCreateRequest.uses(), streamFlowCreateRequest.linkedAppConnNames(),dssLabels);
            LOGGER.info("end to create stream flow {} and id is {}", streamFlowCreateRequest.flowName(), dssFlow.getId());
            return dssFlow;
        }catch(final Throwable t){
            LOGGER.error("Failed to create flow {} for user {} ", streamFlowCreateRequest.flowName(), streamFlowCreateRequest.createBy());
            throw new StreamisFlowErrorException(600401, "Failed to create streamflow", t);
        }
    }

    @Override
    public ExportResponse exportStreamFlow(StreamFlowExportRequest streamFlowExportRequest) throws StreamisFlowErrorException{
        try{
            LOGGER.info("begin to export stream flow {} for user {}", streamFlowExportRequest.streamFlowId(), streamFlowExportRequest.streamFlowId());
            Map<String, Object> exportResponse = workFlowManager.exportWorkflow(streamFlowExportRequest.username(), streamFlowExportRequest.streamFlowId(),
                    streamFlowExportRequest.projectId(), streamFlowExportRequest.projectName(),
                    DSSCommonUtils.COMMON_GSON.fromJson(streamFlowExportRequest.workspaceStr(), Workspace.class));
            String resourceId = exportResponse.get("resourceId").toString();
            String version = exportResponse.get("version").toString();
            LOGGER.info("End to export stream flow, resourceId is {}, version is {}", resourceId, version);
            return new ExportResponse(resourceId, version);
        }catch(final Throwable t){
            LOGGER.error("failed to export stream flow {} for user {}", streamFlowExportRequest.streamFlowId(), streamFlowExportRequest.username());
            throw new StreamisFlowErrorException(600402, "Failed to export streamflow", t);
        }
    }

    @Override
    public List<DSSFlow> importStreamFlow(StreamFlowImportRequest streamFlowImportRequest) throws StreamisFlowErrorException {
        try{
            LOGGER.info("begin to import flow {} for user {}, resourceId is {}, version is {}", streamFlowImportRequest.streamFlowName(),
                    streamFlowImportRequest.username(), streamFlowImportRequest.bmlResourceId(), streamFlowImportRequest.bmlVersion());
            DSSFlowImportParam dssFlowImportParam = new DSSFlowImportParam();
            dssFlowImportParam.setProjectID(streamFlowImportRequest.projectId());
            dssFlowImportParam.setProjectName(streamFlowImportRequest.projectName());
            //todo 这个env先这么写吧
            dssFlowImportParam.setSourceEnv(IOEnv.BDAP_PRODUCTION);
            dssFlowImportParam.setUserName(streamFlowImportRequest.username());
            dssFlowImportParam.setOrcVersion(streamFlowImportRequest.orchestratorVersion());
            dssFlowImportParam.setWorkspaceName(streamFlowImportRequest.workspaceName());
            dssFlowImportParam.setWorkspace(DSSCommonUtils.COMMON_GSON.fromJson(streamFlowImportRequest.workspaceStr(),Workspace.class));
            List<DSSFlow> dssFlows = workFlowManager.importWorkflow(streamFlowImportRequest.username(),
                    streamFlowImportRequest.bmlResourceId(),
                    streamFlowImportRequest.bmlVersion(),
                    dssFlowImportParam);
            LOGGER.info("end to import stream flow and dssFlowId are is {}", Arrays.toString(dssFlows.stream().map(DSSFlow::getId).toArray()));
            return dssFlows;
        }catch(final Throwable t){
            LOGGER.error("failed to import import stream flow {} for user {}", streamFlowImportRequest.streamFlowName(), streamFlowImportRequest.username());
            throw new StreamisFlowErrorException(600405, "failed to import stream flow", t);
        }
    }

    @Override
    public void updateStreamFlow() {

    }

    @Override
    public void publishStreamFlow() {

    }

    @Override
    public DSSFlow copyStreamFlow(StreamFlowCopyRequest streamFlowCopyRequest) throws StreamisFlowErrorException{
        try{
            LOGGER.info("begin to copy stream flow {} for user {}", streamFlowCopyRequest.streamFlowId(), streamFlowCopyRequest.username());
            DSSFlow dssFlow = workFlowManager.copyRootflowWithSubflows(streamFlowCopyRequest.username(),
                    streamFlowCopyRequest.streamFlowId(),streamFlowCopyRequest.workspaceName(),
                    streamFlowCopyRequest.projectName(), streamFlowCopyRequest.contextId(),
                    streamFlowCopyRequest.version(), streamFlowCopyRequest.description());
            LOGGER.error("end to copy stream flow {} for user {}, and result flow is {}", streamFlowCopyRequest.streamFlowId(), streamFlowCopyRequest.username(), dssFlow.getId());
            return dssFlow;
        }catch(final Throwable t){
            LOGGER.error("failed to copy stream flow {} for user{}", streamFlowCopyRequest.streamFlowId(), streamFlowCopyRequest.username());
            throw new StreamisFlowErrorException(600406, "failed to stream flow", t);
        }
    }

    @Override
    public void deleteStreamFlow() throws StreamisFlowErrorException {

    }

    @Override
    public DSSFlow getStreamFlow(Long flowId) throws StreamisFlowErrorException {
        try {
            return dssFlowService.getLatestVersionFlow(flowId);
        } catch (DSSErrorException e) {
            LOGGER.error("failed to get dssFlow for {}", flowId);
            throw new StreamisFlowErrorException(600407, "failed to get flow", e);
        }
    }
}
