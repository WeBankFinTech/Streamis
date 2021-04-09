package com.webank.wedatasphere.streamis.datasource.appconn.service;

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.query.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.development.query.RefQueryService;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by yangzhiyue on 2021/4/8
 * Description:
 */
public class StreamisDatasourceQueryService implements RefQueryService {



    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisDatasourceQueryService.class);


    private AppInstance appInstance;



    public StreamisDatasourceQueryService(AppInstance appInstance){
        this.appInstance = appInstance;
    }





    @Override
    public RefQueryOperation getRefQueryOperation() {
        return null;
    }

    @Override
    public DevelopmentService getDevelopmentService() {
        return null;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {

    }
}
