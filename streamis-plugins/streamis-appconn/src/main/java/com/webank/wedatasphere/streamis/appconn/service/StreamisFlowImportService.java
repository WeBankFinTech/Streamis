package com.webank.wedatasphere.streamis.appconn.service;

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefImportService;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.streamis.appconn.operation.StreamFlowImportOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by yangzhiyue on 2021/4/26
 * Description:
 */
public class StreamisFlowImportService implements RefImportService {


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisFlowImportService.class);

    private final AppInstance appInstance;

    private DevelopmentService developmentService;

    private StreamFlowImportOperation streamFlowImportOperation;


    public StreamisFlowImportService(AppInstance appInstance){
        this.appInstance = appInstance;
        init();
    }

    private void init() {
        LOGGER.info("init in Steamis flow import service");
        this.streamFlowImportOperation = new StreamFlowImportOperation(appInstance);
    }

    @Override
    @SuppressWarnings("unchecked")
    public StreamFlowImportOperation createRefImportOperation() {
        return this.streamFlowImportOperation;
    }

    @Override
    public DevelopmentService getDevelopmentService() {
        return this.developmentService;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }
}
