package com.webank.wedatasphere.streamis.jobmanager.appconn.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.streamis.jobmanager.appconn.utils.AppConnUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/16
 * Description:
 */
public abstract class AbstractResponseRef implements ResponseRef {


    private String responseBody;

    private Map<String, Object> responseMap;

    private int status;

    private String errorMsg;


    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractResponseRef.class);


    public AbstractResponseRef(String responseBody){
        this.responseBody = responseBody;
        init();
    }

    @SuppressWarnings("unchecked")
    private void init(){
        try{
            this.responseMap = AppConnUtils.COMMON_GSON.fromJson(responseBody, Map.class);
            //todo 先这么写
            status = (Integer) responseMap.get("status");
            errorMsg = responseMap.get("errorMsg").toString();
        }catch(final Exception e){
            LOGGER.error("failed to transfer to map for {}", responseBody, e);
            responseMap = new HashMap<>();
            status = -1;
            errorMsg = "responseStr is not a regular json";
        }
    }



    @Override
    public Object getValue(String s) {
        return this.responseMap.get(s);
    }

    @Override
    public Map<String, Object> toMap() {
        return this.responseMap;
    }

    @Override
    public String getResponseBody() {
        return this.responseBody;
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }
}
