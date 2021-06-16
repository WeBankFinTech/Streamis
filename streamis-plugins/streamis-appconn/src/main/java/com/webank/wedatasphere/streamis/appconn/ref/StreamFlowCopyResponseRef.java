package com.webank.wedatasphere.streamis.appconn.ref;

import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorCopyResponseRef;

import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/23
 * Description:
 */
public class StreamFlowCopyResponseRef implements OrchestratorCopyResponseRef {


    private Long oldStreamFlowId;

    private Long newStreamFlowId;

    private String streamFlowContent;

    private String responseBody;

    public StreamFlowCopyResponseRef(Long oldStreamFlowId, Long newStreamFlowId, String streamFlowContent){
        this.oldStreamFlowId = oldStreamFlowId;
        this.newStreamFlowId = newStreamFlowId;
        this.streamFlowContent = streamFlowContent;
    }

    @Override
    public long getCopyOrcId() {
        return this.oldStreamFlowId;
    }

    @Override
    public long getCopyTargetAppId() {
        return this.newStreamFlowId;
    }

    @Override
    public String getCopyTargetContent() {
        return this.streamFlowContent;
    }

    @Override
    public Object getValue(String s) {
        return null;
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public String getResponseBody() {
        return null;
    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public String getErrorMsg() {
        return null;
    }
}
