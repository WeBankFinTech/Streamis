package com.webank.wedatasphere.streamis.jobmanager.appconn.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;

import java.util.HashMap;
import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/15
 * Description:
 */
public class TransformRequestRef implements RequestRef {


    private Long streamisTableMetaId;


    private Map<String, Object> params = new HashMap<>();


    public TransformRequestRef(Long streamisTableMetaId){
        this.streamisTableMetaId = streamisTableMetaId;
    }

    @Override
    public Object getParameter(String s) {
        return params.get(s);
    }

    @Override
    public void setParameter(String s, Object o) {
        params.put(s, o);
    }

    @Override
    public Map<String, Object> getParameters() {
        return this.params;
    }

    @Override
    public String getName() {
        return "TransformRequestRef";
    }

    @Override
    public String getType() {
        return "transform";
    }
}
