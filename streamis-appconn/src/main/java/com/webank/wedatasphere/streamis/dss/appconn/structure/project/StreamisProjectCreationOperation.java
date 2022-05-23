package com.webank.wedatasphere.streamis.dss.appconn.structure.project;

import com.webank.wedatasphere.dss.common.entity.project.DSSProject;
import com.webank.wedatasphere.dss.common.utils.DSSCommonUtils;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSPostAction;
import com.webank.wedatasphere.dss.standard.app.structure.AbstractStructureOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectCreationOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.DSSProjectContentRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.DSSProjectPrivilege;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.InternalResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.streamis.dss.appconn.exception.StreamisAppConnErrorException;
import com.webank.wedatasphere.streamis.dss.appconn.utils.StreamisCommonUtil;

import java.util.List;

import static com.webank.wedatasphere.streamis.dss.appconn.constraints.Constraints.API_REQUEST_PREFIX;
import static com.webank.wedatasphere.streamis.dss.appconn.constraints.Constraints.STREAMIS_APPCONN_NAME;

public class StreamisProjectCreationOperation extends AbstractStructureOperation<DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl, ProjectResponseRef>
        implements ProjectCreationOperation<DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl> {

    private String projectUrl;

    @Override
    protected String getAppConnName() {
        return STREAMIS_APPCONN_NAME;
    }

    @Override
    public ProjectResponseRef createProject(DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl dssProjectContentRequestRef) throws ExternalOperationFailedException {
        DSSPostAction streamisPostAction = new DSSPostAction();
        streamisPostAction.setUser(dssProjectContentRequestRef.getUserName());
        DSSProject dssProject = dssProjectContentRequestRef.getDSSProject();
        DSSProjectPrivilege dssProjectPrivilege = dssProjectContentRequestRef.getDSSProjectPrivilege();
        if(dssProject == null || dssProjectPrivilege == null){
            //TODO error code need to amend
            throw new StreamisAppConnErrorException(-1, "the dssProject or dssProjectPrivilege is null");
        }
        streamisPostAction.addRequestPayload("projectName",dssProjectContentRequestRef.getDSSProject().getName());
        streamisPostAction.addRequestPayload("description", dssProjectContentRequestRef.getDSSProject().getDescription());
        List<String> releaseUsers = dssProjectContentRequestRef.getDSSProjectPrivilege().getReleaseUsers();
        List<String> editUsers = dssProjectContentRequestRef.getDSSProjectPrivilege().getEditUsers();
        List<String> accessUsers = dssProjectContentRequestRef.getDSSProjectPrivilege().getAccessUsers();
        streamisPostAction.addRequestPayload("releaseUsers",releaseUsers);
        streamisPostAction.addRequestPayload("editUsers",editUsers);
        streamisPostAction.addRequestPayload("accessUsers",accessUsers);
        InternalResponseRef responseRef = StreamisCommonUtil.getInternalResponseRef(dssProjectContentRequestRef, ssoRequestOperation, projectUrl, streamisPostAction);
        @SuppressWarnings("unchecked")
        Long projectId = DSSCommonUtils.parseToLong(responseRef.getData().get("projectId"));
        return ProjectResponseRef.newExternalBuilder()
                .setRefProjectId(projectId.longValue()).success();
    }

    @Override
    public void init() {
        super.init();
        projectUrl = mergeBaseUrl(mergeUrl(API_REQUEST_PREFIX, "createProject"));
    }
}
