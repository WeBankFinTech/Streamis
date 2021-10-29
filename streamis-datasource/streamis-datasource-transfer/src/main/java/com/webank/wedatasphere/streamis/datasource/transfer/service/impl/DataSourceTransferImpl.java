package com.webank.wedatasphere.streamis.datasource.transfer.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.manager.label.entity.engine.EngineType;
import com.webank.wedatasphere.linkis.manager.label.entity.engine.RunType;
import com.webank.wedatasphere.streamis.datasource.manager.domain.StreamisDatasourceExtraInfo;
import com.webank.wedatasphere.streamis.datasource.manager.domain.StreamisDatasourceFields;
import com.webank.wedatasphere.streamis.datasource.manager.domain.StreamisTableMeta;
import com.webank.wedatasphere.streamis.datasource.manager.service.StreamisDatasourceExtraInfoService;
import com.webank.wedatasphere.streamis.datasource.manager.service.StreamisDatasourceFieldsService;
import com.webank.wedatasphere.streamis.datasource.manager.service.StreamisTableMetaService;
import com.webank.wedatasphere.streamis.datasource.manager.client.LinkisDataSourceClient;
import com.webank.wedatasphere.streamis.datasource.transfer.entity.StreamisDataSourceCode;
import com.webank.wedatasphere.streamis.datasource.transfer.entity.StreamisTableEntity;
import com.webank.wedatasphere.streamis.datasource.transfer.service.DataSourceTransfer;
import com.webank.wedatasphere.streamis.datasource.transfer.service.LinkisDataSource;
import com.webank.wedatasphere.streamis.datasource.transfer.util.DataSourceTransferFlinksqlUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DataSourceTransferImpl implements DataSourceTransfer {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceTransferImpl.class);

    @Autowired
    StreamisTableMetaService streamisTableMetaService;
    @Autowired
    StreamisDatasourceFieldsService streamisDatasourceFieldsService;

    @Autowired
    StreamisDatasourceExtraInfoService streamisDatasourceExtraInfoService;

    @Override
    public StreamisDataSourceCode transfer(StreamisTableEntity streamisTableEntity, Map<String, Object> labels, Map<String, Object> params) {
        String runType = (String)params.get("runType");
        String engineType = (String)params.get("engineType");
        String version = (String)params.get("version");
        List<StreamisDatasourceExtraInfo> extraInfo = (List<StreamisDatasourceExtraInfo>) params.get("extraInfo");
        //获取表名，列名
        StreamisDataSourceCode streamisDataSourceCode = new StreamisDataSourceCode();
        String tableName = streamisTableEntity.getTableInfo().getTableName();
        List<StreamisDatasourceFields> streamisFields = streamisTableEntity.getFields();
        String cols = "";
        for(StreamisDatasourceFields  streamisField : streamisFields) {
            cols = streamisField.getFieldName() +" "+ streamisField.getFieldType() +",";
        }

        if(EngineType.FLINK().equals(engineType) && version.equals("1.12.2") && RunType.SQL().equals(runType)){
            Map<String, Object> linkisDataSourceContent = streamisTableEntity.getLinkisDatasource().getLinkisDataSourceContent();

            String dataSourceType = linkisDataSourceContent.get("dataSourceType").toString();
            if(dataSourceType.equals("kafka")){
//                String groupId = getKaflaGroupIdInfo(streamisTableEntity.getTableInfo().getId());
                String groupId = getKaflaGroupIdInfo(extraInfo, "kafka.group.id");
                linkisDataSourceContent.put("groupId",groupId);
                linkisDataSourceContent.put("topicName",tableName);
                streamisDataSourceCode = transferKafkaSql(tableName, cols, labels, linkisDataSourceContent);
            }else if(dataSourceType.equals("mysql")){
                linkisDataSourceContent.put("dbName",params.get("nodeName"));
                streamisDataSourceCode = transferMysql(tableName, cols, labels, linkisDataSourceContent);
            }
        }
        return streamisDataSourceCode;
    }

    public StreamisDataSourceCode transferKafkaSql(String tableName, String cols, Map<String, Object> labels, Map<String, Object> linkisDataSourceContent) {
        String servers = (String)linkisDataSourceContent.get("brokers");
        String topicName = (String)linkisDataSourceContent.get("topicName");
        String groupId = (String)linkisDataSourceContent.get("groupId");
        String offsetMode = (String)linkisDataSourceContent.getOrDefault("offsetMode","earliest-offset");
        String formatMode = (String)linkisDataSourceContent.getOrDefault("formatMode","json");
        String executionCode = DataSourceTransferFlinksqlUtils.kafkaDataSourceTransfer(tableName , cols , topicName , servers , groupId , offsetMode , formatMode);
        StreamisDataSourceCode streamisDataSourceCode = new StreamisDataSourceCode(executionCode, labels, new HashMap<String, Object>());
        return streamisDataSourceCode;
    }

    public StreamisDataSourceCode transferMysql(String tableName, String cols, Map<String, Object> labels, Map<String, Object> linkisDataSourceContent){
        String serverIp = (String)linkisDataSourceContent.get("host");//serverIp
        String dbName = (String)linkisDataSourceContent.get("dbName");
        String executionCode = DataSourceTransferFlinksqlUtils.mysqlDataSourceTransfer(tableName,cols,serverIp,dbName);
        StreamisDataSourceCode streamisDataSourceCode = new StreamisDataSourceCode(executionCode, labels, new HashMap<String, Object>());
        return streamisDataSourceCode;
    }


    @Override
    public StreamisTableEntity getStreamisTableMetaById(String streamisTableMetaId, String userName, Long dataSourceId) throws ErrorException{
        StreamisTableEntity streamisTableEntity = new StreamisTableEntity();
        try {
            //查询表信息
            StreamisTableMeta tableInfo = streamisTableMetaService.getById(streamisTableMetaId);
            //查询列信息
            QueryWrapper<StreamisDatasourceFields> streamisDatasourceFieldsWrapper = new QueryWrapper<>();
            streamisDatasourceFieldsWrapper.eq("streamis_table_meta_id",streamisTableMetaId);
            List<StreamisDatasourceFields> fields = streamisDatasourceFieldsService.list(streamisDatasourceFieldsWrapper);
            //查询linkis数据源信息
            String linkisDatasourceName = tableInfo.getLinkisDatasourceName();
            String workspaceName = tableInfo.getWorkspaceName();
            String linkisDataSourceUniqueId = workspaceName + "_" + linkisDatasourceName;
            //通过linkisDatasourceName查询linkis数据源信息
//            QueryDataSourceResult queryDataSourceResult = LinkisDataSourceClient.queryDataSource(linkisDatasourceName);
            Map<String, Object> linkisDataSourceContent = LinkisDataSourceClient.queryConnectParams(dataSourceId, "system", userName).getConnectParams();
            //拿到linkis数据源里的连接信息
//            Map<String, Object> linkisDataSourceContent = queryDataSourceResult.getQuery_list().get(0);
            LinkisDataSource linkisDatasource = new LinkisDataSource(linkisDataSourceUniqueId, linkisDataSourceContent);

            //返回实体
            streamisTableEntity.setTableInfo(tableInfo);
            streamisTableEntity.setFields(fields);
            streamisTableEntity.setLinkisDatasource(linkisDatasource);
        } catch (Exception e) {
            logger.error("the param streamisTableMetaId {} Failed to query metadata(查询元数据失败)：", streamisTableMetaId, e);
            throw new ErrorException(30203, "Sorry, the background query for streamis metadata failed(抱歉，后台查询streamis元数据失败)");
        }
        return streamisTableEntity;
    }


    public String getKaflaGroupIdInfo(List<StreamisDatasourceExtraInfo> list ,String key){
        String value ="";
        if(CollectionUtils.isNotEmpty(list)){
            for (StreamisDatasourceExtraInfo extraInfo : list) {
                if(key.equals(extraInfo.getKey())){
                    value = extraInfo.getValue();
                    break;
                }
            }
        }
        return  value;
    }
}
