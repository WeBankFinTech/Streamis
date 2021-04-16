package com.webank.wedatasphere.streamis.jobmanager.appconn.service;

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.RefOperationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by yangzhiyue on 2021/4/15
 * Description: TransformService是为了将工作流进行转换成的jobManager能够执行的内容
 */
public class TransformService implements RefOperationService {


    private static final Logger LOGGER = LoggerFactory.getLogger(TransformService.class);

    private DevelopmentService developmentService;






    @Override
    public DevelopmentService getDevelopmentService() {
        return this.developmentService;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }
}
