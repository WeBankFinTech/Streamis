package com.webank.wedatasphere.streamis.appconn.operation;

import com.webank.wedatasphere.dss.standard.app.sso.origin.request.OriginSSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.rpc.Sender;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import com.webank.wedatasphere.streamis.appconn.exception.StreamisExternalException;
import com.webank.wedatasphere.streamis.appconn.ref.StreamisProjectResponseRef;
import com.webank.wedatasphere.streamis.appconn.service.StreamisProjectService;
import com.webank.wedatasphere.streamis.plugins.common.StreamisPluginUtils;
import com.webank.wedatasphere.streamis.project.common.CreateStreamProjectRequest;
import com.webank.wedatasphere.streamis.project.common.CreateStreamProjectResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * this is the project creation operation for streamis
 * streamis will build a specific project architecture too
 */
public class StreamisProjectCreationOperation implements ProjectCreationOperation {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisProjectCreationOperation.class);


    private String projectUrl;

    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    private StreamisProjectService streamisProjectService;

    private final String PROJECT_CREATE_URL = "/api/rest_j/v1/streamis/createProject";

    private Sender sender;

    private AppInstance appInstance;

    public StreamisProjectCreationOperation(StreamisProjectService streamisProjectService){
        this.streamisProjectService = streamisProjectService;
        this.ssoRequestOperation = new OriginSSORequestOperation(this.streamisProjectService.getAppDesc().getAppName());
        this.projectUrl = this.streamisProjectService.getAppInstance().getBaseUrl() + PROJECT_CREATE_URL;
    }

    public StreamisProjectCreationOperation(AppInstance appInstance){
        this.appInstance = appInstance;
    }

    private void init(){
        LOGGER.info("begin to init in streamis project creation operation");
        this.sender = Sender.getSender(StreamisPluginUtils.STREAMIS_RPC_SERVER_NAME + "-" + StreamisPluginUtils.getLabel(appInstance));
    }



    /**
     * create project operation
     * @param projectRequestRef
     * @return
     * @throws ExternalOperationFailedException
     */
    @Override
    public StreamisProjectResponseRef createProject(ProjectRequestRef projectRequestRef) throws StreamisExternalException {
        LOGGER.info("begin to create project in streamis, request is {}", BDPJettyServerHelper.gson().toJson(projectRequestRef));
        //todo 工程创建
        CreateStreamProjectRequest createStreamProjectRequest = new CreateStreamProjectRequest(projectRequestRef.getName(),
                projectRequestRef.getDescription(), projectRequestRef.getCreateBy());
        try{
            CreateStreamProjectResponse createStreamProjectResponse = (CreateStreamProjectResponse)sender.ask(createStreamProjectRequest);
            Integer status = createStreamProjectResponse.status();
            String errorMsg = createStreamProjectResponse.errorMessage();
            Long projectId = createStreamProjectResponse.streamisProjectId();
            LOGGER.info("end to create project, status is {} projectId is {}, errorMsg is {}", status, projectId, errorMsg);
            if (createStreamProjectResponse.status() != 0){
                LOGGER.error("status is {}, it means failed to create project, and error is {}", createStreamProjectResponse.status(), createStreamProjectResponse.errorMessage());
                throw new ErrorException(status, errorMsg);
            } else{
                return new StreamisProjectResponseRef(projectId);
            }
        }catch(final Throwable throwable){
            LOGGER.error("fail to ask to create project {} for user {}", projectRequestRef.getName(),projectRequestRef.getCreateBy());
            throw new StreamisExternalException(600310, "failed to create project", throwable);
        }
    }

    @Override
    public void setStructureService(StructureService structureService) {

    }
}
