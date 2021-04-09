package com.webank.wedatasphere.streamis.datasource.appconn.operation;

import com.webank.wedatasphere.dss.standard.app.development.DevelopmentService;
import com.webank.wedatasphere.dss.standard.app.development.query.RefQueryOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import com.webank.wedatasphere.streamis.datasource.appconn.ref.DataSourceQueryResponseRef;
import com.webank.wedatasphere.streamis.datasource.appconn.ref.DatasourceQueryRequestRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by yangzhiyue on 2021/4/8
 * Description:
 * 这个operation的操作是通过的http的方式去的streamis-datasource去获取
 */
public class StreamisDatasourceQueryOperation implements RefQueryOperation<DatasourceQueryRequestRef, DataSourceQueryResponseRef> {


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisDatasourceQueryOperation.class);




    @Override
    public DataSourceQueryResponseRef query(DatasourceQueryRequestRef datasourceQueryRequestRef) throws ExternalOperationFailedException {
        return null;
    }

    @Override
    public void setDevelopmentService(DevelopmentService developmentService) {

    }
}
