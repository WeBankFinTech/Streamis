package com.webank.wedatasphere.streamis.datasource.transfer.entity;

import com.webank.wedatasphere.streamis.datasource.manager.domain.StreamisDatasourceFields;
import com.webank.wedatasphere.streamis.datasource.manager.domain.StreamisTableMeta;
import com.webank.wedatasphere.streamis.datasource.transfer.service.LinkisDataSource;

import java.util.List;

public class StreamisTableEntity {
    private LinkisDataSource linkisDatasource;  //和linkis的相关信息，数据源的连接信息，数据源的元信息
    private List<StreamisDatasourceFields> fields;    //字段信息
    private StreamisTableMeta tableInfo; // 名字，创建人等信息

    public StreamisTableEntity(){}

    public StreamisTableEntity(LinkisDataSource linkisDatasource, List<StreamisDatasourceFields> fields, StreamisTableMeta tableInfo) {
        this.linkisDatasource = linkisDatasource;
        this.fields = fields;
        this.tableInfo = tableInfo;
    }

    public LinkisDataSource getLinkisDatasource() {
        return linkisDatasource;
    }

    public void setLinkisDatasource(LinkisDataSource linkisDatasource) {
        this.linkisDatasource = linkisDatasource;
    }

    public List<StreamisDatasourceFields> getFields() {
        return fields;
    }

    public void setFields(List<StreamisDatasourceFields> fields) {
        this.fields = fields;
    }

    public StreamisTableMeta getTableInfo() {
        return tableInfo;
    }

    public void setTableInfo(StreamisTableMeta tableInfo) {
        this.tableInfo = tableInfo;
    }

    public String toString(){

        return null;
    }
}
