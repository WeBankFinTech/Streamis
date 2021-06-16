package com.webank.wedatasphere.streamis.appconn.ref;

import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;
import com.webank.wedatasphere.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/6
 * Description:
 */
public class StreamisProjectResponseRef extends AbstractResponseRef implements ProjectResponseRef {


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisProjectResponseRef.class);

    private String errorMsg;

    private Long projectRefId;


    public StreamisProjectResponseRef(){
        this("", 0, "");
    }


    public StreamisProjectResponseRef(Long projectRefId){
        this();
        this.projectRefId = projectRefId;
    }

    public StreamisProjectResponseRef(String responseBody, int status, String errorMsg) {
        super(responseBody, status);
        this.errorMsg = errorMsg;
    }

    @Override
    public Long getProjectRefId() {
        return this.projectRefId;
    }

    @Override
    public Map<AppInstance, Long> getProjectRefIds() {
        //todo not decided to return things yet
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> toMap() {
       try{
           return BDPJettyServerHelper.gson().fromJson(this.getResponseBody(), Map.class);
       }catch(final Exception e){
           LOGGER.warn("failed to from json, repsonse body is {}", this.getResponseBody());
           return new HashMap<>();
       }
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }
}
