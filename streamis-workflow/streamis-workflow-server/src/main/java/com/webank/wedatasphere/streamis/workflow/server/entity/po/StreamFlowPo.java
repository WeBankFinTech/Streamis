package com.webank.wedatasphere.streamis.workflow.server.entity.po;

/**
 * created by yangzhiyue on 2021/4/26
 * Description:
 */
public class StreamFlowPo {

    private Long id;
    private Long dssFlowId;
    private String flowName;
    private String createBy;
    private String createdTime;
    private String source;
    private String lastUpdateBy;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDssFlowId() {
        return dssFlowId;
    }

    public void setDssFlowId(Long dssFlowId) {
        this.dssFlowId = dssFlowId;
    }

    public String getFlowName() {
        return flowName;
    }

    public void setFlowName(String flowName) {
        this.flowName = flowName;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }
}
