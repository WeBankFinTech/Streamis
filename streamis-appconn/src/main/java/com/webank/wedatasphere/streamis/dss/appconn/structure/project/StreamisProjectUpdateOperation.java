package com.webank.wedatasphere.streamis.dss.appconn.structure.project;

import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.label.LabelRouteVO;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPutAction;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectUpdateOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectUpdateRequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.streamis.dss.appconn.StreamisAppConn;
import com.webank.wedatasphere.streamis.dss.appconn.utils.StreamisCommonUtil;
import org.apache.linkis.server.conf.ServerConfiguration;

public class StreamisProjectUpdateOperation extends AbstractStructureOperation<ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl, ProjectResponseRef>
        implements ProjectUpdateOperation<ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl> {

    public final static String PROJECT_UPDATE_URL = "/api/rest_j" + ServerConfiguration.BDP_SERVER_VERSION() + "/streamis/updateProject";

    @Override
    protected String getAppConnName() {
        return StreamisAppConn.STREAMIS_APPCONN_NAME;
    }

    @Override
    public ResponseRef updateProject(ProjectUpdateRequestRef.ProjectUpdateRequestRefImpl projectUpdateRequestRef) throws ExternalOperationFailedException {
        String url = getBaseUrl() + PROJECT_UPDATE_URL;
        DSSPutAction updateAction = new DSSPutAction();
        LabelRouteVO routeVO = new LabelRouteVO();
        routeVO.setRoute(((EnvDSSLabel) (projectUpdateRequestRef.getDSSLabels().get(0))).getEnv());
        updateAction.addRequestPayload("labels", routeVO);
        updateAction.setUser(projectUpdateRequestRef.getUserName());
        updateAction.setParameter("id", projectUpdateRequestRef.getRefProjectId());
        return StreamisCommonUtil.getExternalResponseRef(projectUpdateRequestRef, ssoRequestOperation, url, updateAction);
    }

}


