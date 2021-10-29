package com.webank.wedatasphere.streamis.appconn.ref;

import com.webank.wedatasphere.dss.orchestrator.core.ref.OrchestratorCreateResponseRef;
import com.webank.wedatasphere.streamis.appconn.utils.StreamisUtils;

import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/21
 * Description:
 */
public class StreamFlowCreateResponseRef implements OrchestratorCreateResponseRef {


    private Long streamWorkflowId;

    private String content;

    private Long orchestratorId;

    public String responseBody;

    private Map<String, Object> responseMap;


    public StreamFlowCreateResponseRef(Long streamWorkflowId){
        this.streamWorkflowId = streamWorkflowId;
    }

    public StreamFlowCreateResponseRef(String responseBody){
        this.responseBody = responseBody;
        init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        this.responseMap = StreamisUtils.COMMON_GSON.fromJson(responseBody, Map.class);
        //todo 将fields进行set
        //this.streamWorkflowId = this.responseMap.get("data")
    }


    public Long getStreamWorkflowId() {
        return streamWorkflowId;
    }

    public void setStreamWorkflowId(Long streamWorkflowId) {
        this.streamWorkflowId = streamWorkflowId;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public Long getOrchestratorId() {
        return orchestratorId;
    }

    @Override
    public void setOrchestratorId(Long orchestratorId) {
        this.orchestratorId = orchestratorId;
    }

    @Override
    public Object getValue(String s) {
        return null;
    }

    @Override
    public Long getOrchestratorVersionId() {
        return null;
    }

    @Override
    public void setOrchestratorVersionId(Long aLong) {

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
