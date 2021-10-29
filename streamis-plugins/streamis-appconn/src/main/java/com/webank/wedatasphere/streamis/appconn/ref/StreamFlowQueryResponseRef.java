package com.webank.wedatasphere.streamis.appconn.ref;

import com.webank.wedatasphere.dss.orchestrator.common.entity.OrchestratorVo;
import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorQueryResponseRef;

import java.util.List;
import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/23
 * Description:
 */
public class StreamFlowQueryResponseRef implements OrchestratorQueryResponseRef {


    private String responseBody;

    private Long streamFlowId;

    private String streamFlowContent;

    private List<OrchestratorVo> orchestratorVos;


    public StreamFlowQueryResponseRef(String responseBody, Long streamFlowId, String streamFlowContent){
        this.responseBody = responseBody;
        this.streamFlowId = streamFlowId;
        this.streamFlowContent = streamFlowContent;
    }



    @Override
    public List<OrchestratorVo> getOrchestratorVos() {
        return this.orchestratorVos;
    }

    @Override
    public void setOrchestratorVoList(List<OrchestratorVo> orchestratorVos) {
        this.orchestratorVos = orchestratorVos;
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
        return this.responseBody;
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
