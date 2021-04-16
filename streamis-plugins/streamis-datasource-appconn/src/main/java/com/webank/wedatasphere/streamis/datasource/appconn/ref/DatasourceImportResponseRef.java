package com.webank.wedatasphere.streamis.datasource.appconn.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;

import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/13
 * Description:
 */
public class DatasourceImportResponseRef extends AbstractResponseRef {


    private Map<String, Object> bmlResource;

    public DatasourceImportResponseRef(String responseBody, int status) {
        super(responseBody, status);
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
