package com.webank.wedatasphere.streamis.jobmanager.appconn.operation;

import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.service.Operation;
import com.webank.wedatasphere.streamis.jobmanager.appconn.ref.TransformRequestRef;
import com.webank.wedatasphere.streamis.jobmanager.appconn.ref.TransformResponseRef;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by yangzhiyue on 2021/4/15
 * Description:
 * Transform Action要从生产中心的的
 */
public class TransformOperation implements Operation<TransformRequestRef, TransformResponseRef> {





    private AppInstance appInstance;

    private static final Logger LOGGER = LoggerFactory.getLogger(TransformOperation.class);

    private String transformUrl;


    private static final String TRANSFORM_URL = "api/rest_j/v1/streamis/exportDatasource";

    private SSORequestOperation<HttpRequest, HttpResponse> ssoRequestOperation;


    public TransformOperation(AppInstance appInstance){
        this.appInstance = appInstance;
        init();
    }

    private void init(){
        this.transformUrl = this.appInstance.getBaseUrl();
    }



    public TransformResponseRef transform(TransformRequestRef transformRequestRef){
        return null;
        //todo
    }



}
