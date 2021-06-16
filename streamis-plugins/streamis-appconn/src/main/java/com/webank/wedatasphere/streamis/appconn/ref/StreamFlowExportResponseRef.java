package com.webank.wedatasphere.streamis.appconn.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;
import com.webank.wedatasphere.streamis.appconn.utils.StreamisUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/21
 * Description:
 */
public class StreamFlowExportResponseRef extends AbstractResponseRef {


    private String bmlResourceId;

    private String bmlVersion;

    private Long streamFlowId;

    private String errorMsg;


    private Map<String, Object> responseMap;

    //this is for rpc way
    public StreamFlowExportResponseRef(String bmlResourceId, String bmlVersion){
        super("", 0);
        this.bmlResourceId = bmlResourceId;
        this.bmlVersion = bmlVersion;
    }


    //this is for http way
    public StreamFlowExportResponseRef(String responseBody, int status){
        super(responseBody, status);
        init();
    }

    @SuppressWarnings("unchecked")
    private void init() {
        try{
            this.responseMap = StreamisUtils.COMMON_GSON.fromJson(this.responseBody, Map.class);
        } catch(final Exception e){
            this.responseMap = new HashMap<>();
        }
    }

    @Override
    public Map<String, Object> toMap() {
        return this.responseMap;
    }

    public void setErrorMsg(String errorMsg){
        this.errorMsg = errorMsg;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }



}
