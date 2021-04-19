package com.webank.wedatasphere.streamis.jobmanager.appconn.ref;

import com.webank.wedatasphere.dss.standard.app.development.WorkspaceRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;

import java.util.HashMap;
import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/15
 * Description:
 */
public class TransformRequestRef implements WorkspaceRequestRef {


    private Long streamisTableMetaId;


    private Map<String, Object> params = new HashMap<>();

    private Workspace workspace;


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

    @Override
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public Workspace getWorkspace() {
        return this.workspace;
    }

    public Long getStreamisTableMetaId(){
        return this.streamisTableMetaId;
    }

    @Override
    public String toString() {
        return "TransformRequestRef{" +
                "streamisTableMetaId=" + streamisTableMetaId +
                ", params=" + params +
                '}';
    }
}
