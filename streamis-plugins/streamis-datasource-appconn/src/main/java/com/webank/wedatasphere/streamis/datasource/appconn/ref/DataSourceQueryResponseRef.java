package com.webank.wedatasphere.streamis.datasource.appconn.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;

import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/8
 * Description:
 */
public class DataSourceQueryResponseRef implements ResponseRef {


    private String responseBody;

    private Map<String, Object> keyValues;


    @Override
    public Object getValue(String s) {
        return this.keyValues.get(s);
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

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
        //keyValues needs to be set
        this.keyValues =
    }

}
