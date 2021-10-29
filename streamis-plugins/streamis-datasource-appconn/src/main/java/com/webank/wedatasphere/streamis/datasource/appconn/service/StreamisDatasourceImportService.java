package com.webank.wedatasphere.streamis.datasource.appconn.service;

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefImportService;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;

/**
 * created by yangzhiyue on 2021/4/13
 * Description:
 */
public class StreamisDatasourceImportService implements RefImportService {





    @Override
    public <K extends RequestRef, V extends ResponseRef> RefImportOperation<K, V> createRefImportOperation() {
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
