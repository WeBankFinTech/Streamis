package com.webank.wedatasphere.streamis.dss.appconn.structure.project;

import com.webank.wedatasphere.dss.common.label.EnvDSSLabel;
import com.webank.wedatasphere.dss.common.label.LabelRouteVO;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.DSSProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.streamis.dss.appconn.StreamisAppConn;
import com.webank.wedatasphere.streamis.dss.appconn.utils.StreamisCommonUtil;
import org.apache.linkis.server.conf.ServerConfiguration;

public class StreamisProjectCreationOperation extends AbstractStructureOperation<DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl, ProjectResponseRef>
        implements ProjectCreationOperation<DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl> {

    private final static String PROJECT_CREATE_URL = "/api/rest_j/" + ServerConfiguration.BDP_SERVER_VERSION() + "/streamis/createProject";

    @Override
    protected String getAppConnName() {
        return StreamisAppConn.STREAMIS_APPCONN_NAME;
    }

    @Override
    public ProjectResponseRef createProject(DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl dssProjectContentRequestRef) throws ExternalOperationFailedException {
        String url = getBaseUrl() + PROJECT_CREATE_URL;
        DSSPostAction streamisPostAction = new DSSPostAction();
        streamisPostAction.setUser(dssProjectContentRequestRef.getDSSProject().getCreateBy());
        streamisPostAction.addRequestPayload("name", dssProjectContentRequestRef.getName());
        streamisPostAction.addRequestPayload("description", dssProjectContentRequestRef.getDSSProject().getDescription());
        streamisPostAction.addRequestPayload("pic", "6");
        streamisPostAction.addRequestPayload("visibility", true);
        LabelRouteVO routeVO = new LabelRouteVO();
        routeVO.setRoute(((EnvDSSLabel) (dssProjectContentRequestRef.getDSSLabels().get(0))).getEnv());
        streamisPostAction.addRequestPayload("labels", routeVO);
        ResponseRef responseRef = StreamisCommonUtil.getExternalResponseRef(dssProjectContentRequestRef, ssoRequestOperation, url, streamisPostAction);
        @SuppressWarnings("unchecked")
        Long projectId = DSSCommonUtils.parseToLong(responseRef.getValue("projectId"));
        return ProjectResponseRef.newExternalBuilder()
                .setRefProjectId(projectId.longValue()).success();
    }


}
