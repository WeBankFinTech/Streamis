package com.webank.wedatasphere.streamis.appconn.service;

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefPublishToSchedulerService;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.OperationalRefScheduleOperation;
import com.webank.wedatasphere.dss.standard.app.development.publish.scheduler.RefScheduleOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;

/**
 * created by yangzhiyue on 2021/4/26
 * Description:
 */
public class StreamisFlowPublishService implements RefPublishToSchedulerService {


    private DevelopmentService developmentService;

    private AppInstance appInstance;


    public StreamisFlowPublishService(AppInstance appInstance){
        this.appInstance = appInstance;
    }



    @Override
    public RefScheduleOperation createRefScheduleOperation() {
        return null;
    }

    @Override
    public OperationalRefScheduleOperation createOperationalRefScheduleOperation(RequestRef requestRef) {
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
