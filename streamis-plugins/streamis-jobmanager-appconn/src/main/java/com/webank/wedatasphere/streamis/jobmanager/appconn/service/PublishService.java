package com.webank.wedatasphere.streamis.jobmanager.appconn.service;

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefPublishToLinkisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by yangzhiyue on 2021/4/15
 * Description:
 * PublishService 主要是将streamis的工作流publish到JobManager
 */
public class PublishService implements RefPublishToLinkisService {


    private static final Logger LOGGER = LoggerFactory.getLogger(PublishService.class);

    private DevelopmentService developmentService;






    @Override
    public DevelopmentService getDevelopmentService() {
        return null;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {

    }
}
