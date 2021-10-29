package com.webank.wedatasphere.streamis.datasource.appconn.service;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.common.entity.DSSLabel;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefExportService;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.streamis.datasource.appconn.operation.DatasourceExportOperation;
import com.webank.wedatasphere.streamis.datasource.appconn.utils.DataSourceUtils;

/**
 * created by yangzhiyue on 2021/4/13
 * Description:
 */
public class StreamisDatasourceExportService implements RefExportService {




    private AppConn appConn;


    private DatasourceExportOperation datasourceExportOperation;


    private DevelopmentService developmentService;



    public StreamisDatasourceExportService(AppConn appConn){
        this.appConn = appConn;
        AppInstance realInstance = null;
        for (AppInstance appInstance : appConn.getAppDesc().getAppInstances()) {
            for (DSSLabel label : appInstance.getLabels()) {
                if (DataSourceUtils.DEV.equalsIgnoreCase(label.getLabel())){
                    realInstance = appInstance;
                    break;
                }
            }
            if (realInstance != null) break;
        }
        this.datasourceExportOperation = new DatasourceExportOperation(realInstance);
    }




    @Override
    @SuppressWarnings("unchecked")
    public DatasourceExportOperation createRefExportOperation() {
        return this.datasourceExportOperation;
    }

    @Override
    public DevelopmentService getDevelopmentService() {
        return null;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }
}
