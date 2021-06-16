package com.webank.wedatasphere.streamis.appconn.service;

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.crud.*;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.entity.ref.RequestRef;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by yangzhiyue on 2021/4/8
 * Description: streamis crud service是通过http的方式进行的
 */
public class StreamisFlowCRUDService implements RefCRUDService {



    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisFlowCRUDService.class);

    private AppInstance appInstance;

    public StreamisFlowCRUDService(AppInstance appInstance){
        this.appInstance = appInstance;
    }


    @Override
    public <K extends CreateRequestRef, V extends ResponseRef> RefCreationOperation<K, V> createTaskCreationOperation() {
        return null;
    }

    @Override
    public <K extends CopyRequestRef, V extends ResponseRef> RefCopyOperation<K, V> createRefCopyOperation() {
        return null;
    }

    @Override
    public <K extends RequestRef, V extends ResponseRef> RefUpdateOperation<K, V> createRefUpdateOperation() {
        return null;
    }

    @Override
    public <K extends DeleteRequestRef, V extends ResponseRef> RefDeletionOperation<K, V> createRefDeletionOperation() {
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
