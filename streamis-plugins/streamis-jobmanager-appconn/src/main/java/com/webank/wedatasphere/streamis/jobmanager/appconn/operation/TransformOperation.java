package com.webank.wedatasphere.streamis.jobmanager.appconn.operation;

import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.OriginSSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.standard.common.service.Operation;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.streamis.jobmanager.appconn.action.JMPostAction;
import com.webank.wedatasphere.streamis.jobmanager.appconn.ref.TransformRequestRef;
import com.webank.wedatasphere.streamis.jobmanager.appconn.ref.TransformResponseRef;
import com.webank.wedatasphere.streamis.jobmanager.appconn.utils.AppConnUtils;
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

    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;


    public TransformOperation(AppInstance appInstance){
        this.appInstance = appInstance;
        init();
    }

    private void init(){
        this.transformUrl = this.appInstance.getBaseUrl().endsWith("/")? this.appInstance.getBaseUrl() + TRANSFORM_URL :
                this.appInstance.getBaseUrl() + "/" + TRANSFORM_URL;
        this.ssoRequestOperation = new OriginSSORequestOperation(AppConnUtils.STREAMIS_NAME);
    }



    /**
     * to transform streamisMetaId to code can be executed
     * @param transformRequestRef
     * @return
     */
    public TransformResponseRef transform(TransformRequestRef transformRequestRef) throws ExternalOperationFailedException {
        SSOUrlBuilderOperation ssoUrlBuilderOperation = transformRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(AppConnUtils.STREAMIS_NAME);
        ssoUrlBuilderOperation.setReqUrl(this.transformUrl);
        ssoUrlBuilderOperation.setWorkspace(transformRequestRef.getWorkspace().getWorkspaceName());
        String responseBody = "";
        try{
            LOGGER.info("begin to transform transformRequestRef {}", transformRequestRef);
            JMPostAction jmPostAction = new JMPostAction(ssoUrlBuilderOperation.getBuiltUrl());
            jmPostAction.addRequestPayload("streamisMetaTableId", transformRequestRef.getStreamisTableMetaId());
            HttpResult httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, jmPostAction);
            responseBody = httpResult.getResponseBody();
            LOGGER.info("end to transform transformRequestRef {}, responseBody is {}", transformRequestRef, responseBody);
        }catch(final Exception e){
            DSSExceptionUtils.dealErrorException(30702, "failed to transfrom", e, ExternalOperationFailedException.class);
        }
        return new TransformResponseRef(responseBody);
    }


}
