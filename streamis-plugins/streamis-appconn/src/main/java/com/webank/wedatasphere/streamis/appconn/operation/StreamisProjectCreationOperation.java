package com.webank.wedatasphere.streamis.appconn.operation;

import com.webank.wedatasphere.dss.standard.app.sso.origin.request.OriginSSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import com.webank.wedatasphere.streamis.appconn.service.StreamisProjectService;
import org.apache.http.client.HttpClient;
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


    public StreamisProjectCreationOperation(StreamisProjectService streamisProjectService){
        this.streamisProjectService = streamisProjectService;
        this.ssoRequestOperation = new OriginSSORequestOperation(this.streamisProjectService.getAppDesc().getAppName());
        this.projectUrl = this.streamisProjectService.getAppInstance().getBaseUrl() + PROJECT_CREATE_URL;
    }


    /**
     * create project operation
     * @param projectRequestRef
     * @return
     * @throws ExternalOperationFailedException
     */
    @Override
    public ProjectResponseRef createProject(ProjectRequestRef projectRequestRef) throws ExternalOperationFailedException {
        LOGGER.info("begin to create project in streamis, request is {}", BDPJettyServerHelper.gson().toJson(projectRequestRef));

    }

    @Override
    public void setStructureService(StructureService structureService) {

    }
}
