package com.webank.wedatasphere.streamis.datasource.appconn.operation;

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.publish.RefImportOperation;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.streamis.datasource.appconn.action.DatasourcePostAction;
import com.webank.wedatasphere.streamis.datasource.appconn.exception.StreamisDatasourceExternalException;
import com.webank.wedatasphere.streamis.datasource.appconn.ref.DatasourceImportRequestRef;
import com.webank.wedatasphere.streamis.datasource.appconn.ref.DatasourceImportResponseRef;
import com.webank.wedatasphere.streamis.datasource.appconn.utils.DataSourceUtils;
import com.webank.wedatasphere.streamis.plugins.common.StreamisPluginUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/26
 * Description:
 */
public class DatasourceImportOperation implements RefImportOperation<DatasourceImportRequestRef, DatasourceImportResponseRef> {



    private AppInstance appInstance;

    private static final Logger LOGGER = LoggerFactory.getLogger(DatasourceImportOperation.class);

    private String datasourceImportUrl;

    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    private static final String DATASOURCE_SUFFIX_URL = "api/res_j/v1/streamis/importDatasource";


    public DatasourceImportOperation(AppInstance appInstance){
        this.appInstance = appInstance;
    }


    @Override
    public DatasourceImportResponseRef importRef(DatasourceImportRequestRef datasourceImportRequestRef) throws StreamisDatasourceExternalException {
        LOGGER.info("begin to export datasource from streamis, request is {}", DataSourceUtils.COMMON_GSON.toJson(datasourceImportRequestRef));
        try{
            SSOUrlBuilderOperation ssoUrlBuilderOperation = datasourceImportRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
            ssoUrlBuilderOperation.setAppName(DataSourceUtils.DATASOURCE_APP_NAME.getValue());
            ssoUrlBuilderOperation.setReqUrl(this.datasourceImportUrl);
            ssoUrlBuilderOperation.setWorkspace(datasourceImportRequestRef.getWorkspace().getWorkspaceName());
            DatasourcePostAction datasourcePostAction = new DatasourcePostAction(ssoUrlBuilderOperation.getBuiltUrl(), datasourceImportRequestRef.getUser());
            datasourcePostAction.addRequestPayload(StreamisPluginUtils.BML_RESOURCE_ID_STR, datasourceImportRequestRef.getBmlResourceId());
            datasourcePostAction.addRequestPayload(StreamisPluginUtils.BML_RESOURCE_VERSION_STR,datasourceImportRequestRef.getBmlVersion());
            HttpResult httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, datasourcePostAction);
            LOGGER.info("end to export datasource from streamis, response is {}", httpResult.getResponseBody());
            Map<String, Object> data = StreamisPluginUtils.getDataFromResponseBody(httpResult.getResponseBody());
            if (data == null){
                LOGGER.error("data from responseBody {} is null", httpResult.getResponseBody());
                throw new StreamisDatasourceExternalException(600401, "failed to import datasource to streamis");
            }
            Long streamisTableMetaId = (Long) data.get(StreamisPluginUtils.STREAMIS_RPC_SERVER_NAME);
            return new DatasourceImportResponseRef(httpResult.getResponseBody(), 0, streamisTableMetaId);
        }catch(final StreamisDatasourceExternalException e){
            throw e;
        }
        catch(final Exception e){
            throw new StreamisDatasourceExternalException(300601, "failed to export ref ", e);
        }
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {

    }
}
