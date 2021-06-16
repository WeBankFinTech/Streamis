package com.webank.wedatasphere.streamis.datasource.transfer.entity;

import com.webank.wedatasphere.linkis.manager.label.entity.Label;

import java.util.List;
import java.util.Map;

public class StreamisDataSourceCode {
    String executionCode;  //执行代码内容
    Map<String, Object> labels; //linkis的标签,按照linkis1.0 rc-1的标签体系
    Map<String,Object> params;  //预留参数

    public StreamisDataSourceCode(){}

    public StreamisDataSourceCode(String executionCode, Map<String, Object> linkisLabels, Map<String, Object> params) {
        this.executionCode = executionCode;
        this.labels = labels;
        this.params = params;
    }

    public String getExecutionCode() {
        return executionCode;
    }

    public void setExecutionCode(String executionCode) {
        this.executionCode = executionCode;
    }

    public Map<String, Object> getLabels() {
        return labels;
    }

    public void setLabels(Map<String, Object> labels) {
        this.labels = labels;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
}
