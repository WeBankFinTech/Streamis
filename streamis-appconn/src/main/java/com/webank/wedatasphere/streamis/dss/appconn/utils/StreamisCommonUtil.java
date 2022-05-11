package com.webank.wedatasphere.streamis.dss.appconn.utils;

import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.action.DSSHttpAction;
import com.webank.wedatasphere.dss.standard.app.sso.ref.WorkspaceRequestRef;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.entity.ref.ResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.dss.standard.sso.utils.SSOHelper;
import com.webank.wedatasphere.streamis.dss.appconn.StreamisAppConn;
import com.webank.wedatasphere.streamis.dss.appconn.structure.ref.StreamisResponseRefBuilder;
import org.apache.linkis.httpclient.request.HttpAction;
import org.apache.linkis.httpclient.response.HttpResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamisCommonUtil {

    private final static Logger logger = LoggerFactory.getLogger(StreamisCommonUtil.class);

    public static SSOUrlBuilderOperation getSSOUrlBuilderOperation(WorkspaceRequestRef requestRef, String url) {
        SSOUrlBuilderOperation ssoUrlBuilderOperation = SSOHelper.createSSOUrlBuilderOperation(requestRef.getWorkspace());
        ssoUrlBuilderOperation.setAppName(StreamisAppConn.STREAMIS_APPCONN_NAME);
        ssoUrlBuilderOperation.setReqUrl(url);
        return ssoUrlBuilderOperation;
    }

    public static HttpResult getHttpResult(WorkspaceRequestRef requestRef,
                                           SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation,
                                           String url,
                                           DSSHttpAction streamisHttpAction) throws ExternalOperationFailedException {

        try {
            SSOUrlBuilderOperation ssoUrlBuilderOperation = getSSOUrlBuilderOperation(requestRef, url);
            streamisHttpAction.setUrl(ssoUrlBuilderOperation.getBuiltUrl());
            return ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, streamisHttpAction);
        } catch (Exception e) {
            throw new ExternalOperationFailedException(90177, "Create streamis node Exception", e);
        }
    }

    public static ResponseRef getExternalResponseRef(WorkspaceRequestRef requestRef,
                                                     SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation,
                                                     String url,
                                                     DSSHttpAction streamisHttpAction) throws ExternalOperationFailedException {
        HttpResult httpResult = getHttpResult(requestRef, ssoRequestOperation, url, streamisHttpAction);
        ResponseRef responseRef = new StreamisResponseRefBuilder().setResponseBody(httpResult.getResponseBody()).build();
        checkResponseRef(responseRef);
        return responseRef;
    }

    public static void checkResponseRef(ResponseRef responseRef) throws ExternalOperationFailedException {
        if (responseRef.getStatus() != 0 && responseRef.getStatus() != 200) {
            logger.error(responseRef.getResponseBody());
            throw new ExternalOperationFailedException(90177, responseRef.getErrorMsg(), null);
        }
    }
}
