package com.webank.wedatasphere.streamis.datasource.appconn.operation;

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefExportOperation;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.OriginSSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.streamis.datasource.appconn.action.DatasourcePostAction;
import com.webank.wedatasphere.streamis.datasource.appconn.ref.DatasourceExportRequestRef;
import com.webank.wedatasphere.streamis.datasource.appconn.ref.DatasourceExportResponseRef;
import com.webank.wedatasphere.streamis.datasource.appconn.utils.DataSourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by yangzhiyue on 2021/4/13
 * Description:
 */
public class DatasourceExportOperation implements RefExportOperation<DatasourceExportRequestRef, DatasourceExportResponseRef> {


    private static final Logger LOGGER = LoggerFactory.getLogger(DatasourceExportOperation.class);


    private String exportUrl;

    private static final String EXPORT_URL = "api/rest_j/v1/streamis/exportDatasource";

    private AppInstance appInstance;

    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    private DevelopmentService developmentService;

    public DatasourceExportOperation(AppInstance appInstance){
        this.appInstance = appInstance;
        exportUrl = this.appInstance.getBaseUrl().endsWith("/") ? this.appInstance.getBaseUrl() + EXPORT_URL :
                this.appInstance.getBaseUrl() + "/" + EXPORT_URL;
        ssoRequestOperation = new OriginSSORequestOperation(DataSourceUtils.DATASOURCE_APP_NAME.getValue());
    }






    @Override
    public DatasourceExportResponseRef exportRef(DatasourceExportRequestRef datasourceExportRequestRef) throws ExternalOperationFailedException {
        LOGGER.info("begin to export datasource from streamis, request is {}", datasourceExportRequestRef);
        try{
            SSOUrlBuilderOperation ssoUrlBuilderOperation = datasourceExportRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
            ssoUrlBuilderOperation.setAppName(DataSourceUtils.DATASOURCE_APP_NAME.getValue());
            ssoUrlBuilderOperation.setReqUrl(this.exportUrl);
            ssoUrlBuilderOperation.setWorkspace(datasourceExportRequestRef.getWorkspace().getWorkspaceName());
            DatasourcePostAction datasourcePostAction = new DatasourcePostAction(ssoUrlBuilderOperation.getBuiltUrl(), datasourceExportRequestRef.getUser());
            datasourcePostAction.addRequestPayload("streamisTableMetaId", datasourceExportRequestRef.getTableMetaId());
            HttpResult httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, datasourcePostAction);
            LOGGER.info("end to export datasource from streamis, response is {}", httpResult.getResponseBody());
            return new DatasourceExportResponseRef(httpResult.getResponseBody());
        }catch(final Exception e){
            throw new ExternalOperationFailedException(300601, "failed to export ref ", e);
        }
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {
        this.developmentService = developmentService;
    }
}
