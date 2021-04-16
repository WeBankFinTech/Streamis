package com.webank.wedatasphere.streamis.datasource.appconn.operation;

import com.google.common.collect.Maps;
import com.webank.wedatasphere.dss.common.utils.DSSExceptionUtils;
import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.query.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.app.sso.builder.SSOUrlBuilderOperation;
import com.webank.wedatasphere.dss.standard.app.sso.origin.request.OriginSSORequestOperation;
import com.webank.wedatasphere.dss.standard.app.sso.request.SSORequestOperation;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.linkis.httpclient.request.HttpAction;
import com.webank.wedatasphere.linkis.httpclient.response.HttpResult;
import com.webank.wedatasphere.streamis.datasource.appconn.action.DatasourcePostAction;
import com.webank.wedatasphere.streamis.datasource.appconn.ref.DataSourceQueryResponseRef;
import com.webank.wedatasphere.streamis.datasource.appconn.ref.DatasourceQueryRequestRef;
import com.webank.wedatasphere.streamis.datasource.appconn.utils.DataSourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/8
 * Description:
 * 这个operation的操作是通过的http的方式去的streamis-datasource去获取
 */
public class StreamisDatasourceQueryOperation implements RefQueryOperation<DatasourceQueryRequestRef, DataSourceQueryResponseRef> {


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisDatasourceQueryOperation.class);


    private AppInstance appInstance;

    private String datasourceQueryUrl;

    private SSORequestOperation<HttpAction, HttpResult> ssoRequestOperation;

    private static final String DATASOURCE_SUFFIX_URL = "api/res_j/streamis/queryDatasource";

    public StreamisDatasourceQueryOperation(AppInstance appInstance){
        this.appInstance = appInstance;
        this.datasourceQueryUrl = this.appInstance.getBaseUrl().endsWith("/") ? this.appInstance + DATASOURCE_SUFFIX_URL :
                this.appInstance + "/" + DATASOURCE_SUFFIX_URL;
        this.ssoRequestOperation = new OriginSSORequestOperation(DataSourceUtils.DATASOURCE_APP_NAME.getValue());
    }


    private Map<String, Object> generateLabels(){
        //todo to generate default labels first
        Map<String, Object> labels = new HashMap<>();
        labels.put("engineType", "flink-1.11.1");
        labels.put("runType", "sql");
        return labels;
    }

    @Override
    @SuppressWarnings("unchecked")
    public DataSourceQueryResponseRef query(DatasourceQueryRequestRef datasourceQueryRequestRef) throws ExternalOperationFailedException {
        //这里就是通过Http的方式和StreamiDataSource交互查询到所信息
        //要通过sso的方式
        String user =  datasourceQueryRequestRef.getUser();
        Long streamisTableMetaId = datasourceQueryRequestRef.getStreamisDatasourceId();
        SSOUrlBuilderOperation ssoUrlBuilderOperation = datasourceQueryRequestRef.getWorkspace().getSSOUrlBuilderOperation().copy();
        ssoUrlBuilderOperation.setAppName(DataSourceUtils.DATASOURCE_APP_NAME.getValue());
        ssoUrlBuilderOperation.setReqUrl(this.datasourceQueryUrl);
        ssoUrlBuilderOperation.setWorkspace(datasourceQueryRequestRef.getWorkspace().getWorkspaceName());
        String response = "";
        Map<String, Object> responseMap = Maps.newHashMap();
        HttpResult httpResult = null;
        try{
            LOGGER.info("begin to query datasource in streamis");
            DatasourcePostAction datasourcePostAction = new DatasourcePostAction(ssoUrlBuilderOperation.getBuiltUrl(), user);
            datasourcePostAction.addRequestPayload("streamisTableMetaId", streamisTableMetaId);
            datasourcePostAction.addRequestPayload("labels", generateLabels());
            httpResult = this.ssoRequestOperation.requestWithSSO(ssoUrlBuilderOperation, datasourcePostAction);
            response = httpResult.getResponseBody();
            LOGGER.info("end to query datasource in streamis, response is {}", response);
            responseMap = DataSourceUtils.COMMON_GSON.fromJson(response, Map.class);
        }catch(final Exception e){
            DSSExceptionUtils.dealErrorException(30001, "faile to query datasource in streamis", e, ExternalOperationFailedException.class);
        }
        DataSourceQueryResponseRef dataSourceQueryResponseRef = new DataSourceQueryResponseRef(response);
        return dataSourceQueryResponseRef;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {

    }
}
