package com.webank.wedatasphere.streamis.datasource.manager.domain;


public class StreamisDataSourceTableVO {
    private String tableName;
    private Boolean isStreamisDataSource;
    private Long streamisTableMetaId;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Boolean getStreamisDataSource() {
        return isStreamisDataSource;
    }

    public void setStreamisDataSource(Boolean streamisDataSource) {
        isStreamisDataSource = streamisDataSource;
    }

    public Long getStreamisTableMetaId() {
        return streamisTableMetaId;
    }

    public void setStreamisTableMetaId(Long streamisTableMetaId) {
        this.streamisTableMetaId = streamisTableMetaId;
    }

    @Override
    public String toString() {
        return "StreamisDataSourceTableVO{" +
                "tableName='" + tableName + '\'' +
                ", isStreamisDataSource=" + isStreamisDataSource +
                ", streamisTableMetaId=" + streamisTableMetaId +
                '}';
    }
}
