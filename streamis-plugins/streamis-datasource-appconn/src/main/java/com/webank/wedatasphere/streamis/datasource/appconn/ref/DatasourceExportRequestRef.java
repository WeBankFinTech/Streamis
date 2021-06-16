package com.webank.wedatasphere.streamis.datasource.appconn.ref;

import com.webank.wedatasphere.dss.standard.app.development.publish.ExportRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.Workspace;
import com.webank.wedatasphere.streamis.datasource.appconn.utils.DataSourceUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/13
 * Description:
 */
public class DatasourceExportRequestRef implements ExportRequestRef {


    private Map<String, Object> parameters = new HashMap<>();

    private final static String NAME = "DatasourceExportRequestRef";

    private final static String TYPE = "export";

    private Workspace workspace;

    private Long tableMetaId = -1L;

    public DatasourceExportRequestRef(Long tableMetaId){
        this.tableMetaId = tableMetaId;
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

    public String getUser(){
        return (String)parameters.get("user");
    }


    @Override
    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public Workspace getWorkspace() {
        return this.workspace;
    }

    @Override
    public String toString() {
        return "DatasourceExportRequestRef{" +
                "parameters=" + DataSourceUtils.COMMON_GSON.toJson(parameters) +
                '}';
    }


    public Long getTableMetaId() {
        if (tableMetaId < 0){
            tableMetaId = (Long)getParameter("streamisTableMetaId");
        }
        return tableMetaId;
    }

    public void setTableMetaId(Long tableMetaId) {
        this.tableMetaId = tableMetaId;
    }
}
