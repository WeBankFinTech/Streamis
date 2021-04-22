package com.webank.wedatasphere.streamis.datasource.transfer.service;

import java.util.Map;

public class LinkisDataSource {
    private String linkisDataSourceUniqueId;
    private Map<String, Object> linkisDataSourceContent;

    public LinkisDataSource(String linkisDataSourceUniqueId, Map<String, Object> linkisDataSourceContent) {
        this.linkisDataSourceUniqueId = linkisDataSourceUniqueId;
        this.linkisDataSourceContent = linkisDataSourceContent;
    }

    public String getLinkisDataSourceUniqueId() {
        return linkisDataSourceUniqueId;
    }

    public void setLinkisDataSourceUniqueId(String linkisDataSourceUniqueId) {
        this.linkisDataSourceUniqueId = linkisDataSourceUniqueId;
    }

    public Map<String, Object> getLinkisDataSourceContent() {
        return linkisDataSourceContent;
    }

    public void setLinkisDataSourceContent(Map<String, Object> linkisDataSourceContent) {
        this.linkisDataSourceContent = linkisDataSourceContent;
    }
}
