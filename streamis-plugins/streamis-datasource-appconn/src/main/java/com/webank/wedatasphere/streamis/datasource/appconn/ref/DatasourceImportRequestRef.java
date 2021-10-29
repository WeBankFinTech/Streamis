package com.webank.wedatasphere.streamis.datasource.appconn.ref;

import com.webank.wedatasphere.dss.standard.app.development.publish.ImportRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.streamis.plugins.common.StreamisPluginUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/13
 * Description:
 */
public class DatasourceImportRequestRef implements ImportRequestRef {


    private Map<String, Object> parameters = new HashMap<>();

    private final static String NAME = "DatasourceImportRequestRef";

    private Workspace workspace;

    private static final String TYPE = "import";

    private String user;

    /**
     * bmlresource includes resourceId and version
     */
    private Map<String, Object> bmlResource;


    public DatasourceImportRequestRef(){

    }


    @Override
    public Object getParameter(String s) {
        return this.parameters.get(s);
    }

    @Override
    public void setParameter(String s, Object o) {
        this.parameters.put(s, o);
    }

    @Override
    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public Workspace getWorkspace() {
        return this.workspace;
    }

    public Map<String, Object> getBmlResource() {
        return bmlResource;
    }

    public String getBmlResourceId(){
        return (String) this.parameters.get(StreamisPluginUtils.BML_RESOURCE_ID_STR);
    }

    public String getBmlVersion(){
        return (String) this.parameters.get(StreamisPluginUtils.BML_RESOURCE_VERSION_STR);
    }

    public String getUser(){
        return (String)this.parameters.get(StreamisPluginUtils.USER_STR);
    }





    public void setBmlResource(Map<String, Object> bmlResource) {
        this.bmlResource = bmlResource;
    }
}
