package com.webank.wedatasphere.streamis.appconn.service;

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefExportService;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.streamis.appconn.operation.StreamFlowExportOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by yangzhiyue on 2021/4/26
 * Description:
 */
public class StreamisFlowExportService implements RefExportService {


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisFlowExportService.class);

    private AppInstance appInstance;

    private DevelopmentService developmentService;

    private StreamFlowExportOperation streamFlowExportOperation;

    public StreamisFlowExportService(AppInstance appInstance) {
        this.appInstance = appInstance;
        this.streamFlowExportOperation = new StreamFlowExportOperation(appInstance);
    }

    @Override
    @SuppressWarnings("unchecked")
    public StreamFlowExportOperation createRefExportOperation() {
        return this.streamFlowExportOperation;
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
