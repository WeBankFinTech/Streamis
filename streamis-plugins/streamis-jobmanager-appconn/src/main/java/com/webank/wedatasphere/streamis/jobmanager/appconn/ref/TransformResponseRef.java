package com.webank.wedatasphere.streamis.jobmanager.appconn.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.streamis.jobmanager.appconn.utils.AppConnUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/15
 * Description:
 * 进行返回的的内容
 */
public class TransformResponseRef implements ResponseRef {


    private Map<String, Object> labels;

    private String responseBody;

    private Map<String, Object> responseMap;


    private static final Logger LOGGER  = LoggerFactory.getLogger(TransformResponseRef.class);

    public TransformResponseRef(String responseBody){
        super();
        this.responseBody = responseBody;
        init();
    }

    @SuppressWarnings("unchecked")
    private void init(){
        try{
            this.responseMap = AppConnUtils.COMMON_GSON.fromJson(this.responseBody, Map.class);
        }catch(final Exception e){
            LOGGER.error("failed from json using {}", this.responseBody, e);
            this.responseMap = new HashMap<>();
        }
    }


    @Override
    public Object getValue(String s) {
        return responseMap.get(s);
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
        return 0;
    }

    @Override
    public String getErrorMsg() {
        return null;
    }
}
