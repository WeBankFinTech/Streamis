package com.webank.wedatasphere.streamis.datasource.transfer.service;

import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.streamis.datasource.transfer.entity.StreamisDataSourceCode;
import com.webank.wedatasphere.streamis.datasource.transfer.entity.StreamisTableEntity;

import java.util.Map;

public interface DataSourceTransfer {
    StreamisDataSourceCode transfer(StreamisTableEntity streamisTableEntity, Map<String, Object> labels, Map<String, Object> params);

    StreamisTableEntity getStreamisTableMetaById(String tableMetaId, String userName, Long dataSourceId) throws ErrorException;

}
