package com.webank.wedatasphere.streamis.appconn.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;

import java.util.List;
import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/26
 * Description:
 */
public class StreamFlowImportResponseRef extends AbstractResponseRef {


    private List<Long> streamFlowIds;

    public StreamFlowImportResponseRef(String responseBody, int status){
        super(responseBody, status);
    }

    public StreamFlowImportResponseRef(List<Long> streamFlowIds){
        super("" , 0);
        this.streamFlowIds = streamFlowIds;
    }

    @Override
    public Map<String, Object> toMap() {
        return null;
    }

    @Override
    public String getErrorMsg() {
        return null;
    }


}
