package com.webank.wedatasphere.streamis.datasource.transfer.service.impl;

import java.util.Map;

public abstract class AbstractLinkisDataSource{
    protected String workspaceName;

    protected String linkisDataSourceName;

    protected String linkisDataSourceUniqueId;

    protected Map<String, Object> linkisDataSourceContent;

    public String getLinkisDataSourceUniqueId() {
        linkisDataSourceUniqueId = this.workspaceName + "_" + this.linkisDataSourceName;
        return linkisDataSourceUniqueId;
    }

    public Map<String, Object> getLinkisDataSourceContent(Map<String, Object> connectParams) {
        //子类的字段信息封装到此处
        return this.linkisDataSourceContent;
    }
}
