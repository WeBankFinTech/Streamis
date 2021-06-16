package com.webank.wedatasphere.streamis.appconn.ref;

import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorCopyRequestRef;

import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/23
 * Description:
 */
public class StreamFlowCopyRequestRef implements OrchestratorCopyRequestRef {

    private Long oldStreamFlowId;

    private String contextIdStr;

    private String projectName;

    private String version;

    private String description;

    private String username;

    private Long orcId;

    private Long orcVersionId;

    /**
     * contextId等信息都只能先写入到params
     */
    private Map<String, Object> params;


    @Override
    public void setCopyOrcAppId(long orcAppId) {
        this.oldStreamFlowId = orcAppId;
    }

    @Override
    public void setCopyOrcVersionId(long orcVersionId) {
        this.orcVersionId = orcVersionId;
    }

    @Override
    public Object getParameter(String key) {
        return this.params.get(key);
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
        return "copy ref";
    }

    @Override
    public String getType() {
        return "COPY";
    }
}
