package com.webank.wedatasphere.streamis.datasource.transfer.service.impl;

import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class KafkaLinkisDataSource extends AbstractLinkisDataSource {
    private String workspaceName;
    private String linkisDataSourceName;
    private String linkisDataSourceUniqueId;
    private Map<String, Object> linkisDataSourceContent;


    public Map<String, Object> getLinkisDataSourceContent(Map<String, Object> connectParams) {
        this.linkisDataSourceContent = connectParams;
        return linkisDataSourceContent;
    }

    public void setWorkspaceName(String workspaceName) {
        this.workspaceName = workspaceName;
    }

    public void setLinkisDataSourceName(String linkisDataSourceName) {
        this.linkisDataSourceName = linkisDataSourceName;
    }
}
