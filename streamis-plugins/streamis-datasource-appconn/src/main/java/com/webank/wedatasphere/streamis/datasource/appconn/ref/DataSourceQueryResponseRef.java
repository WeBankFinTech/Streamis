package com.webank.wedatasphere.streamis.datasource.appconn.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.streamis.datasource.appconn.utils.DataSourceUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/8
 * Description:
 */
public class DataSourceQueryResponseRef implements ResponseRef {


    private String responseBody;

    private String errorMsg;

    private int status;


    private Map<String, Object> keyValues;

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSourceQueryResponseRef.class);

    public DataSourceQueryResponseRef(String responseBody){
        this.setResponseBody(responseBody);
    }



    @Override
    public Object getValue(String s) {
        return this.keyValues.get(s);
    }

    @Override
    public Map<String, Object> toMap() {
        return this.keyValues;
    }

    @Override
    public String getResponseBody() {
        return this.responseBody;
    }

    @Override
    public int getStatus() {
        if (StringUtils.isNotBlank(errorMsg)){
            return -1;
        }
        return 0;
    }


    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }

    @SuppressWarnings("unchecked")
    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
        //keyValues needs to be set
        try{
            this.keyValues = DataSourceUtils.COMMON_GSON.fromJson(responseBody, Map.class);
        }catch(final Throwable t){
            LOGGER.error("failed to convert {} to map", responseBody, t);
            this.keyValues = new HashMap<>();
        }
        //todo 先这么写
        if (keyValues.containsKey("errorMsg")){
            errorMsg = keyValues.get("errorMsg").toString();
        }
    }



}
