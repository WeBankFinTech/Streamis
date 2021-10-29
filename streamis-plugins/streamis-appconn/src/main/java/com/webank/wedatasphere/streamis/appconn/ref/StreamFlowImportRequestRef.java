package com.webank.wedatasphere.streamis.appconn.ref;

import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.common.entity.IOEnv;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorImportRequestRef;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/26
 * Description:
 */
public class StreamFlowImportRequestRef implements OrchestratorImportRequestRef {


    private String bmlResourceId;

    private String bmlVersion;

    private String username;

    private String projectName;

    private Long projectId;

    private String workspaceName;

    private Map<String, Object> params = new HashMap<>();

    private String orchestratorVersion;

    private String streamFlowName;

    @Override
    public void setUserName(String s) {
        this.username = s;
    }

    @Override
    public String getUserName() {
        return this.username;
    }

    @Override
    public void setResourceId(String s) {
        this.bmlResourceId = s;
    }

    @Override
    public String getResourceId() {
        return this.bmlResourceId;
    }

    @Override
    public void setBmlVersion(String s) {
        this.bmlVersion = s;
    }

    @Override
    public String getBmlVersion() {
        return this.bmlVersion;
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
    public void setProjectName(String s) {
        this.projectName = s;
    }

    @Override
    public String getProjectName() {
        return this.projectName;
    }

    @Override
    public void setSourceEnv(IOEnv ioEnv) {

    }

    @Override
    public IOEnv getSourceEnv() {
        return null;
    }

    @Override
    public void setOrcVersion(String s) {
        this.orchestratorVersion = s;
    }

    @Override
    public String getOrcVersion() {
        return this.orchestratorVersion;
    }

    @Override
    public void setWorkspaceName(String s) {
        this.workspaceName = s;
    }

    @Override
    public String getWorkspaceName() {
        return this.workspaceName;
    }

    @Override
    public List<DSSLabel> getDSSLabels() {
        return null;
    }

    @Override
    public void setDSSLabels(List<DSSLabel> list) {

    }

    @Override
    public Object getParameter(String s) {
        return this.params.get(s);
    }

    @Override
    public void setParameter(String s, Object o) {
        this.params.put(s, o);
    }

    @Override
    public Map<String, Object> getParameters() {
        return this.params;
    }

    @Override
    public String getName() {
        return this.streamFlowName;
    }

    @Override
    public String getType() {
        return "import";
    }
}
