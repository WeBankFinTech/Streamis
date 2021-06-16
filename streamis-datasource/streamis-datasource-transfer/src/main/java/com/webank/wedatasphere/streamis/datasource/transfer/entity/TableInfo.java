package com.webank.wedatasphere.streamis.datasource.transfer.entity;

import java.util.List;

public class TableInfo {
    private String tableName;
    private String alias;
    private List<String> tags;
    //作用域 任务级或者工程级等
    private String scope;
    //层级，表示 DWS 或者 DWD层
    private String layer;
    private String description;

    public TableInfo(String tableName, String alias, List<String> tags, String scope, String layer, String description) {
        this.tableName = tableName;
        this.alias = alias;
        this.tags = tags;
        this.scope = scope;
        this.layer = layer;
        this.description = description;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getLayer() {
        return layer;
    }

    public void setLayer(String layer) {
        this.layer = layer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
