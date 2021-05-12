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
import com.webank.wedatasphere.streamis.jobmanager.appconn.ref.PublishRequestRef;
import com.webank.wedatasphere.streamis.jobmanager.appconn.ref.PublishResponseRef;
import com.webank.wedatasphere.streamis.jobmanager.appconn.utils.AppConnUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by yangzhiyue on 2021/4/16
 * Description:
 */
public class PublishOperation implements Operation<PublishRequestRef, PublishResponseRef> {


    private static final Logger LOGGER = LoggerFactory.getLogger(PublishOperation.class);


    private String publishUrl;

    private AppInstance appInstance;

    private static final String DEFAULT_PUBLISH_URL = "api/rest_j/v1/streamis/streamJobManager/job/publishToJobManager";

    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    public PublishOperation(AppInstance appInstance){
        this.appInstance = appInstance;
        init();
    }

    private void init(){
        this.publishUrl = this.appInstance.getBaseUrl().endsWith("/") ? this.appInstance.getBaseUrl() + DEFAULT_PUBLISH_URL :
                this.appInstance.getBaseUrl() + "/" + DEFAULT_PUBLISH_URL;
        this.ssoRequestOperation = new OriginSSORequestOperation(AppConnUtils.STREAMIS_NAME);
    }

    /**
     * to publish a streamis job to Streamis JobManager
     * 1.可以考虑封装成zip包,但是比较麻烦， 可以直接通过json的方式进行publish
     * @param publishRequestRef
     * @return
     */
    public PublishResponseRef publish(PublishRequestRef publishRequestRef) throws ExternalOperationFailedException {
        SSOUrlBuilderOperation ssoUrlBuilderOperation = publishRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setReqUrl(this.publishUrl);
        ssoUrlBuilderOperation.setWorkspace(publishRequestRef.getWorkspace().getWorkspaceName());
        try{
            LOGGER.info("begin to publish publishRequestRef {}", publishRequestRef);
            JMPostAction jmPostAction = new JMPostAction(ssoUrlBuilderOperation.getBuiltUrl());
            jmPostAction.addRequestPayload("streamisJobName", publishRequestRef.getStreamisJobName());
            jmPostAction.addRequestPayload("createBy", publishRequestRef.getCreateBy());
            jmPostAction.addRequestPayload("executionCode", publishRequestRef.getExecutionCode());
            jmPostAction.addRequestPayload("publishUser", publishRequestRef.getPublishUser());
            jmPostAction.addRequestPayload("tags", publishRequestRef.getTags());
            jmPostAction.addRequestPayload("updateBy", publishRequestRef.getUpdateBy());
            jmPostAction.addRequestPayload("description", publishRequestRef.getDescription());
            HttpResult httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, jmPostAction);
            String responseBody = httpResult.getResponseBody();
            LOGGER.info("end to publish {}, respose is {}", publishRequestRef, responseBody);
            return new PublishResponseRef(responseBody);
        }catch(final Exception e){
            DSSExceptionUtils.dealErrorException(30700, "failed to publsih", e, ExternalOperationFailedException.class);
        }
        return null;
    }






}
