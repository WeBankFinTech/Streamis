package com.webank.wedatasphere.streamis.appconn.ref;

import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.orchestrator.common.entity.DSSOrchestratorInfo;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorCreateRequestRef;

import java.util.List;
import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/20
 * Description:
 */
public class StreamFlowCreateRequestRef implements OrchestratorCreateRequestRef {


    private String username;

    private String workspaceName;

    private String projectName;

    private String workflowName;

    private DSSOrchestratorInfo dssOrchestratorInfo;

    private String contextIdStr;

    private Long projectId;

    private Map<String, Object> params;


    /**
     * 父工作流id
     */
    private Long parentFlowId;

    @Override
    public void setUserName(String username) {
        this.username = username;
    }

    @Override
    public String getUserName() {
        return this.username;
    }


    public Long getParentFlowId() {
        return parentFlowId;
    }

    public void setParentFlowId(Long parentFlowId) {
        this.parentFlowId = parentFlowId;
    }

    @Override
    public void setWorkspaceName(String s) {
        this.workflowName = s;
    }

    @Override
    public String getWorkspaceName() {
        return this.workspaceName;
    }

    @Override
    public void setProjectName(String s) {
        this.projectName = s;
    }

    @Override
    public String getProjectName() {
        return this.projectName;
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
    public void setDssOrchestratorInfo(DSSOrchestratorInfo dssOrchestratorInfo) {
        this.dssOrchestratorInfo = dssOrchestratorInfo;
    }

    @Override
    public DSSOrchestratorInfo getDSSOrchestratorInfo() {
        return this.dssOrchestratorInfo;
    }

    @Override
    public void setContextIDStr(String contextIdStr) {
        this.contextIdStr = contextIdStr;
    }

    @Override
    public String getContextIDStr() {
        return this.contextIdStr;
    }

    @Override
    public void setDSSLabels(List<DSSLabel> list) {

    }

    @Override
    public List<DSSLabel> getDSSLabels() {
        return null;
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
        return null;
    }

    @Override
    public String getType() {
        return null;
    }
}
