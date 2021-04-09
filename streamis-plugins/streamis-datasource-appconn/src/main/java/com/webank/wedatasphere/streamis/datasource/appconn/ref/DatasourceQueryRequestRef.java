package com.webank.wedatasphere.streamis.datasource.appconn.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;

import java.util.HashMap;
import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/8
 * Description:
 * this ref is used to get datasource from streamis
 * 这个类主要是用来进行的
 */
public class DatasourceQueryRequestRef implements RequestRef {


    private Map<String, Object> params = new HashMap<>();

    private final String NAME = "DataSourceQueryRequestRef";




    @Override
    public Object getParameter(String s) {
        return this.params.get(s);
    }

    @Override
    public void setParameter(String s, Object o) {
        params.put(s ,o);
    }

    @Override
    public Map<String, Object> getParameters() {
        return this.params;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getType() {
        return null;
    }


}
