package com.webank.wedatasphere.streamis.appconn.service;

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.query.RefQueryService;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.streamis.appconn.operation.StreamFlowQueryOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by yangzhiyue on 2021/4/8
 * Description:
 */
public class StreamisFlowQueryService implements RefQueryService {


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisFlowQueryService.class);


    private AppInstance appInstance;

    private DevelopmentService developmentService;


    private StreamFlowQueryOperation streamFlowQueryOperation;


    public StreamisFlowQueryService(AppInstance appInstance){
        this.appInstance = appInstance;
        init();
    }

    private void init() {
        this.streamFlowQueryOperation = new StreamFlowQueryOperation(appInstance);
    }


    @Override
    public StreamFlowQueryOperation getRefQueryOperation() {
        return this.streamFlowQueryOperation;
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
