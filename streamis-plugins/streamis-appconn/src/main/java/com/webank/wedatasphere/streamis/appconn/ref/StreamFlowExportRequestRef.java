package com.webank.wedatasphere.streamis.appconn.ref;

import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorExportRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import java.util.List;
import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/21
 * Description:
 */
public class StreamFlowExportRequestRef implements OrchestratorExportRequestRef {


    private String username;
    private Long streamFlowId;
    private String streamFlowName;
    private Long projectId;
    private String projectName;
    private Workspace workspace;


    public Long getStreamFlowId() {
        return getAppId();
    }


    @Override
    public void setUserName(String s) {
        this.username = username;
    }


    @Override
    public String getUserName() {
        return this.username;
    }

    @Override
    public void setAppId(Long appId) {
        this.streamFlowId = appId;
    }

    @Override
    public Long getAppId() {
        return this.streamFlowId;
    }

    @Override
    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    @Override
    public Long getProjectId() {
        return this.projectId;
    }

    @Override
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public String getProjectName() {
        return this.projectName;
    }

    @Override
    public void setOrchestratorId(Long aLong) {

    }

    @Override
    public Long getOrchestratorId() {
        return null;
    }

    @Override
    public String getWorkspaceName() {
        return null;
    }

    @Override
    public void setWorkspaceName(String s) {

    }

    @Override
    public void setOrchestratorVersionId(Long aLong) {

    }

    @Override
    public Long getOrchestratorVersionId() {
        return null;
    }

    @Override
    public List<DSSLabel> getDSSLabels() {
        return null;
    }

    @Override
    public void setDSSLabels(List<DSSLabel> list) {

    }

    @Override
    public boolean getAddOrcVersionFlag() {
        return false;
    }

    @Override
    public void setAddOrcVersionFlag(boolean b) {

    }

    @Override
    public Object getParameter(String s) {
        return null;
    }

    @Override
    public void setParameter(String s, Object o) {

    }

    @Override
    public Map<String, Object> getParameters() {
        return null;
    }

    @Override
    public String getName() {
        return streamFlowName;
    }


    @Override
    public String getType() {
        return null;
    }

    @Override
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public Workspace getWorkspace() {
        return this.workspace;
    }
}
