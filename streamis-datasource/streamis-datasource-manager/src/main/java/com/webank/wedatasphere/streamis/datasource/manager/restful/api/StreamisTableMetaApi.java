/*
 * Copyright 2019 WeBank
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.wedatasphere.streamis.datasource.manager.restful.api;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.datasourcemanager.common.domain.DataSource;
import com.webank.wedatasphere.linkis.datasourcemanager.common.domain.DataSourceType;
import com.webank.wedatasphere.linkis.metadatamanager.common.domain.MetaColumnInfo;
import com.webank.wedatasphere.streamis.datasource.manager.domain.*;
import com.webank.wedatasphere.streamis.datasource.manager.service.*;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import com.webank.wedatasphere.streamis.datasource.manager.client.LinkisDataSourceClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StreamisTableMetaApi {

    private static final Logger logger = LoggerFactory.getLogger(StreamisTableMetaApi.class);

    @Autowired
    private StreamisTableMetaService streamisTableMetaService;

    @Autowired
    private StreamisDatasourceFieldsService streamisDatasourceFieldsService;

    @Autowired
    private StreamisDatasourceAuthorityService streamisDatasourceAuthorityService;

    @Autowired
    StreamisDatasourceExtraInfoService streamisDatasourceExtraInfoService;

    @Autowired
    StreamisDatasourceExtraUiService streamisDatasourceExtraUiService;

    ObjectMapper mapper = new ObjectMapper();





    public Response getDataSourceCluster( HttpServletRequest req, Map<String,String> json
    ) throws ErrorException {
        Message message;
        try{
            String userName = SecurityFilter.getLoginUsername(req);
            String system = json.getOrDefault("system","streamis");
            String name = json.get("name");
            Long dataSourceTypeId = Long.valueOf(json.get("dataSourceTypeId"));
            List<DataSource> dataSourceClusters = getDataSourceClusters(system, name, dataSourceTypeId, userName);
            message = Message.ok().data("dataSourceCluster",dataSourceClusters);
        }catch (Throwable e){
            logger.error("failed to get dataSourceType list",e);
            throw new ErrorException(30016, "sorry,failed to get dataSource list");
        }
        return Message.messageToResponse(message);
    }

    public Response getDataSourceTypes( HttpServletRequest req, Map<String,Object> json
    ) throws ErrorException {
        Message message;
        try{
            String userName = SecurityFilter.getLoginUsername(req);
            List<DataSourceType> dataSourceTypes = getDataSourceTypes(userName);
            message = Message.ok().data("dataSourceTypes",dataSourceTypes);
        }catch (Throwable e){
            logger.error("failed to get dataSourceType list",e);
            throw new ErrorException(30015, "sorry,failed to get dataSourceType list");
        }
        return Message.messageToResponse(message);
    }

    public Response getDataBasesByCuster( HttpServletRequest req, Map<String,String> json
    ) throws ErrorException {
        Message message;
        try{
            String userName = SecurityFilter.getLoginUsername(req);
            String system = json.getOrDefault("system","streamis");
            Long dataSourceTypeId = Long.valueOf(json.get("dataSourceId"));
            List<String> dataBases = queryDataBasesByCuster(system, dataSourceTypeId, userName);
            message = Message.ok().data("dataBases",dataBases);
        }catch (Throwable e){
            logger.error("failed to get dataBases list",e);
            throw new ErrorException(30017, "sorry,failed to get dataBases list");
        }
        return Message.messageToResponse(message);
    }

    public Response getTablesByDataBase( HttpServletRequest req, Map<String,String> json
    ) throws ErrorException {
        Message message;
        try{
            String userName = SecurityFilter.getLoginUsername(req);
            String system = json.getOrDefault("system","streamis");
            Long dataSourceId = Long.valueOf(json.get("dataSourceId"));
            String dataBase = json.get("dataBase");
            QueryWrapper<StreamisTableMeta> wrapper = new QueryWrapper<>();;
            List<String> tables = queryTablesByDataBase(system, dataSourceId, dataBase,userName);
            List<StreamisDataSourceTableVO> tableList = new ArrayList<>();
            StreamisDataSourceTableVO tableVO = null;
            wrapper.eq("node_name",dataBase);
            List<StreamisTableMeta> list = streamisTableMetaService.list(wrapper);

            for (String table : tables) {
                if(CollectionUtils.isNotEmpty(list)){
                    tableVO = new StreamisDataSourceTableVO();
                    tableVO.setTableName(table);
                    for (StreamisTableMeta streamisTableMeta : list) {
                        if(table.equals(streamisTableMeta.getTableName())){
                            tableVO.setStreamisTableMetaId(streamisTableMeta.getId());
                            tableVO.setStreamisDataSource(true);
//                            tableList.add(tableVO);
                            break;
                        }
                    }
                    tableList.add(tableVO);

                }else {
                    tableVO = new StreamisDataSourceTableVO();
                    tableVO.setTableName(table);
                    tableVO.setStreamisDataSource(false);
                    tableList.add(tableVO);
                }


            }
            message = Message.ok().data("tables",tableList);
        }catch (Throwable e){
            logger.error("failed to get tables list",e);
            throw new ErrorException(30018, "sorry,failed to get tables list");
        }
        return Message.messageToResponse(message);
    }

    public Response getColumnsByTable( HttpServletRequest req, Map<String,String> json
    ) throws ErrorException {
        Message message;
        try{
            String userName = SecurityFilter.getLoginUsername(req);
            String system = json.getOrDefault("system","streamis");
            Long dataSourceTypeId = Long.valueOf(json.get("dataSourceId"));
            String dataBase = json.get("dataBase");
            String table = json.get("table");
            String dataSourceType = json.get("dataSourceType");
            List<MetaColumnInfo> columns =null;
            if(!"kafka".equalsIgnoreCase(dataSourceType)){
                columns = queryColumnsByTable(system, dataSourceTypeId, dataBase, table, userName);
            }
            QueryWrapper<StreamisDatasourceExtraUi> wrapper = new QueryWrapper<>();
            wrapper.eq("datasource_type",dataSourceType);
            List<StreamisDatasourceExtraUi> extraUis = streamisDatasourceExtraUiService.list(wrapper);
            message = Message.ok().data("columns",columns).data("extraUis",extraUis);
        }catch (Throwable e){
            logger.error("failed to get columns list",e);
            throw new ErrorException(30017, "sorry,failed to get columns list");
        }
        return Message.messageToResponse(message);
    }

    public Response getStreamisTableMeta( HttpServletRequest req, Long streamisTableMetaId
    ) throws ErrorException {
        Message message;
        try{
            StreamisTableMeta streamisTableMeta = streamisTableMetaService.getById(streamisTableMetaId);
            QueryWrapper<StreamisDatasourceFields> streamisDatasourceFieldsWrapper = new QueryWrapper<>();
            streamisDatasourceFieldsWrapper.eq("streamis_table_meta_id",streamisTableMetaId);
            List<StreamisDatasourceFields> streamisDatasourceFields = streamisDatasourceFieldsService.list(streamisDatasourceFieldsWrapper);

            QueryWrapper<StreamisDatasourceExtraInfo> datasourceExtraInfoQueryWrapper = new QueryWrapper<>();

            datasourceExtraInfoQueryWrapper.eq("streamis_table_meta_id",streamisTableMetaId);

            List<StreamisDatasourceExtraInfo> streamisExtraInfo = streamisDatasourceExtraInfoService.list(datasourceExtraInfoQueryWrapper);

            QueryWrapper<StreamisDatasourceAuthority> streamisDatasourceAuthorityQueryWrapper = new QueryWrapper<>();
            streamisDatasourceAuthorityQueryWrapper.eq("streamis_table_meta_id",streamisTableMetaId);

            StreamisDatasourceAuthority streamisDatasourceAuthorityInfo = streamisDatasourceAuthorityService.getOne(streamisDatasourceAuthorityQueryWrapper);

            message=Message.ok().data("streamisTableMeta",streamisTableMeta).data("streamisDatasourceFields",streamisDatasourceFields).data("streamisExtraInfo",streamisExtraInfo).data("streamisDatasourceAuthorityInfo",streamisDatasourceAuthorityInfo);

        }catch (Throwable e){
            logger.error("Failed to get streamisTableMetaInfo",e);
            throw new ErrorException(30002, "Failed to get streamisTableMetaInfo(后台获取数据源详细信息失败)");
        }
        return Message.messageToResponse(message);

    }

    public Response getStreamisTableMetaByNodeNames( HttpServletRequest req,JsonNode json
    ) throws ErrorException {
        Message message;
        QueryWrapper<StreamisTableMeta> wrapper = null;
        Map<String,List<StreamisTableMeta>> nodeTablesMap = new HashMap<>();
        List<StreamisTableMeta> list = null;
        try{
            List<String> nodeNames = mapper.readValue(json.get("nodeNames"), new TypeReference<List<String>>() {});

            if(CollectionUtils.isEmpty(nodeNames)){
                logger.error("Failed to get streamisDataSource Tree,node_name is null");
                throw new ErrorException(30012, "Failed to get streamisDataSource Tree,node_name is null");
            }
            for (String nodeName : nodeNames) {
                wrapper = new QueryWrapper<>();
                wrapper.eq("node_name",nodeName);
                list = streamisTableMetaService.list(wrapper);
                nodeTablesMap.put(nodeName,list);
            }
            message = Message.ok().data("nodeTables", nodeTablesMap);

        }catch (Throwable e){

            logger.error("Failed to get streamisDataSource Tree",e);
            throw new ErrorException(30011, e.getMessage());
        }
        return Message.messageToResponse(message);

    }


    public Response addStreamisTableMeta( HttpServletRequest req,  JsonNode json) throws ErrorException {
        Message message;
        try {
            String userName = SecurityFilter.getLoginUsername(req);
            String authorityId = json.get("authorityId").asText();

            StreamisTableMeta streamisTableMeta = mapper.readValue(json.get("streamisTableMeta"), StreamisTableMeta.class);
            //第一次保存linkisDataSource数据源
            streamisTableMeta.setCreateBy(userName);
            boolean result = streamisTableMetaService.save(streamisTableMeta);
            if(!result){
                throw new ErrorException(30003, "Sorry, failed to add streamis data source(抱歉，添加streamis数据源失败_");
            }
            Long streamisTableMetaId = streamisTableMeta.getId();

            //add StreamisDatasourceFields
            List<StreamisDatasourceFields> fieldsList = mapper.readValue(json.get("streamisTableFields"), new TypeReference<List<StreamisDatasourceFields>>() {
            });

            fieldsList.forEach(obj ->obj.setStreamisTableMetaId(streamisTableMetaId));
            result = streamisDatasourceFieldsService.saveBatch(fieldsList);
            if(!result){
                throw new ErrorException(30013, "Sorry, failed to add StreamisTableFields table field information(抱歉，添加StreamisTableFields表字段信息失败)");
            }
//            String scope = streamisTableMeta.getScope();
            StreamisDatasourceAuthority streamisDatasourceAuthority = new StreamisDatasourceAuthority();

            streamisDatasourceAuthority.setAuthorityScope(streamisTableMeta.getScope());
            streamisDatasourceAuthority.setStreamisTableMetaId(streamisTableMetaId);
            streamisDatasourceAuthority.setAuthorityId(authorityId);
            streamisDatasourceAuthority.setGrantUser("*");
            result = streamisDatasourceAuthorityService.save(streamisDatasourceAuthority);
            if(!result){
                throw new ErrorException(30014, " sorry, add streamisDatasourceAuthority information failure (抱歉，添加streamisDatasourceAuthority信息失败)");
            }
            List<StreamisDatasourceExtraInfo> extraInfoList = mapper.readValue(json.get("streamisExtraInfo"), new TypeReference<List<StreamisDatasourceExtraInfo>>() {
            });

            extraInfoList.forEach(obj-> obj.setStreamisTableMetaId(streamisTableMetaId));

            result = streamisDatasourceExtraInfoService.saveBatch(extraInfoList);
            if(!result){
                throw new ErrorException(30019, " sorry, add extraInfo information failure");
            }
            //add to StreamisDatasourceAuthority
            message = Message.ok().data("streamisTableMetaId",streamisTableMetaId);
        } catch (Exception e) {
            logger.error("Failed to add StreamisTableMeta: ", e);
            throw new ErrorException(30003, e.getMessage());
        }
        return  Message.messageToResponse(message);
    }


    /**
     *  保存数据源，新增，修改，删除字段等
     * @param req
     * @param json
     * @return
     * @throws ErrorException
     */
    public Response saveStreamisTableMeta( HttpServletRequest req,  JsonNode json) throws ErrorException {
        Message message;
        try {
            String userName = SecurityFilter.getLoginUsername(req);
//            String authorityId = json.get("authorityId").asText();
            StreamisDatasourceAuthority streamisDatasourceAuthority = mapper.readValue(json.get("streamisDatasourceAuthority"), StreamisDatasourceAuthority.class);
            StreamisTableMeta streamisTableMeta = mapper.readValue(json.get("streamisTableMeta"), StreamisTableMeta.class);
            List<StreamisDatasourceFields> fieldsList = mapper.readValue(json.get("streamisTableFields"), new TypeReference<List<StreamisDatasourceFields>>() {
            });
            List<StreamisDatasourceExtraInfo> extraInfoList = mapper.readValue(json.get("streamisExtraInfo"), new TypeReference<List<StreamisDatasourceExtraInfo>>() {
            });

            Long streamisTableMetaId =0L;
            // add streamisTableMeta
            if(null == streamisTableMeta.getId()){
                //第一次保存linkisDataSource数据源
                streamisTableMeta.setCreateBy(userName);
                boolean result = streamisTableMetaService.save(streamisTableMeta);
                if(!result){
                    throw new ErrorException(30003, "Sorry, failed to add streamis data source(抱歉，添加streamis数据源失败_");
                }
                streamisTableMetaId = streamisTableMeta.getId();

                //add StreamisDatasourceFields

                if(CollectionUtils.isNotEmpty(fieldsList)){
                    fieldsList.forEach(obj ->obj.setStreamisTableMetaId(streamisTableMeta.getId()));
                    result = streamisDatasourceFieldsService.saveBatch(fieldsList);
                    if(!result){
                        throw new ErrorException(30013, "Sorry, failed to add StreamisTableFields table field information(抱歉，添加StreamisTableFields表字段信息失败)");
                    }
                }

                streamisDatasourceAuthority.setAuthorityScope(streamisTableMeta.getScope());
                streamisDatasourceAuthority.setStreamisTableMetaId(streamisTableMetaId);
                if(StringUtils.isBlank(streamisDatasourceAuthority.getGrantUser())){
                    streamisDatasourceAuthority.setGrantUser("*");
                }
//                streamisDatasourceAuthority.setAuthorityId(authorityId);
//                streamisDatasourceAuthority.setGrantUser("*");
                result = streamisDatasourceAuthorityService.save(streamisDatasourceAuthority);
                if(!result){
                    throw new ErrorException(30014, " sorry, add streamisDatasourceAuthority information failure (抱歉，添加streamisDatasourceAuthority信息失败)");
                }

                List<StreamisDatasourceExtraInfo> insertList = extraInfoList.stream().filter(obj -> obj.getId() == null).collect(Collectors.toList());

                if(CollectionUtils.isNotEmpty(insertList)){
                    extraInfoList.forEach(obj-> obj.setStreamisTableMetaId(streamisTableMeta.getId()));
                    result = streamisDatasourceExtraInfoService.saveBatch(extraInfoList);
                    if(!result){
                        throw new ErrorException(30019, " sorry, add extraInfo information failure");
                    }
                }

            }else { //update steamisTableMeta Info
                boolean b = streamisTableMetaService.updateById(streamisTableMeta);
                streamisTableMetaId = streamisTableMeta.getId();
                if(!b){
                    throw new ErrorException(30004, "sorry，Failed to update streamis data source information(更新streamis数据源信息失败)");
                }

                //delete streamis fields
                List<Long> deleteFields = deleteStreamisFields(fieldsList, streamisTableMetaId);
                if(CollectionUtils.isNotEmpty(deleteFields)){
                    b = streamisDatasourceFieldsService.removeByIds(deleteFields);
                    if(!b){
                        throw new ErrorException(30007, "sorry，Failed to delete StreamisTableFields information(删除StreamisTableFields表字段信息失败)");
                    }
                }
                //update streamis fields
                List<StreamisDatasourceFields> streamisDatasourceFields = updateStreamisFields(fieldsList, streamisTableMetaId);
                if(CollectionUtils.isNotEmpty(streamisDatasourceFields)){
                    b = streamisDatasourceFieldsService.updateBatchById(streamisDatasourceFields);
                    if(!b){
                        throw new ErrorException(30006, "Failed to update StreamisTableFields information(抱歉，更新StreamisTableFields表字段信息失败)");
                    }
                }
                //insert streamis fields
                List<StreamisDatasourceFields> insertStreamisFields = insertStreamisFields(fieldsList,streamisTableMetaId);
                if(CollectionUtils.isNotEmpty(insertStreamisFields)){
                    b = streamisDatasourceFieldsService.saveBatch(insertStreamisFields);
                    if(!b){
                        throw new ErrorException(30005, "Failed to add StreamisTableFields information(添加StreamisTableFields表字段信息失败)");
                    }
                }
                List<StreamisDatasourceExtraInfo> updateExtraInfoList = updateExtraInfo(extraInfoList, streamisTableMetaId);
                if(CollectionUtils.isNotEmpty(updateExtraInfoList)){
                    b = streamisDatasourceExtraInfoService.updateBatchById(updateExtraInfoList);
                    if(!b){
                        throw new ErrorException(30021, "sorry,failed to update streamisExtraInfo");
                    }
                }

            }
            //add to StreamisDatasourceAuthority
            message = Message.ok().data("streamisTableMetaId",streamisTableMetaId);
        } catch (Exception e) {
            logger.error("Failed to add StreamisTableMeta: ", e);
            throw new ErrorException(30003, e.getMessage());
        }
        return  Message.messageToResponse(message);
    }


    public Response updateStreamisTableMeta( HttpServletRequest req,  JsonNode json) throws ErrorException {
        Message message;
        try {
            String userName = SecurityFilter.getLoginUsername(req);
            StreamisTableMeta streamisTableMeta = mapper.readValue(json.get("streamisTableMeta"), StreamisTableMeta.class);
            Long streamisTableMetaId = streamisTableMeta.getId();
            if(null == streamisTableMetaId ){
                throw new ErrorException(30004, "sorry，Failed to update streamis data source information(更新streamis数据源信息失败)，streamisTableMetaId is null");
            }
            streamisTableMeta.setUpdateBy(userName);
            // update StreamisTableMeta
            streamisTableMetaService.updateById(streamisTableMeta);

            //更新字段信息；
//            StreamisDatasourceFields streamisDatasourceFields = mapper.readValue(json.get("streamisTableFields"), StreamisDatasourceFields.class);
//            streamisDatasourceFieldsService.updateById(streamisDatasourceFields);

            List<StreamisDatasourceFields> streamisDatasourceFields = mapper.readValue(json.get("streamisTableFields"), new TypeReference<List<StreamisDatasourceFields>>() {
            });
            if(CollectionUtils.isNotEmpty(streamisDatasourceFields)){
                boolean b = streamisDatasourceFieldsService.updateBatchById(streamisDatasourceFields);
                if(!b){
                    throw new ErrorException(30021, "sorry，Failed to update the streamisDatasourceFields information(更新字段信息失败)");
                }
            }

            List<StreamisDatasourceExtraInfo> extraInfoList = mapper.readValue(json.get("streamisExtraInfo"), new TypeReference<List<StreamisDatasourceExtraInfo>>() {
            });

            if(CollectionUtils.isNotEmpty(extraInfoList)){
                extraInfoList.forEach(obj-> obj.setStreamisTableMetaId(streamisTableMetaId));
                UpdateWrapper<StreamisDatasourceExtraInfo> wrapper = null;
                boolean result ;
                for (StreamisDatasourceExtraInfo extraInfo : extraInfoList) {
                    wrapper = new UpdateWrapper<>();
                    wrapper.eq("key",extraInfo.getKey()).eq("streamis_table_meta_id",streamisTableMetaId).set("value",extraInfo.getValue());
                    result = streamisDatasourceExtraInfoService.update(null, wrapper);
                    if(!result){
                        throw new ErrorException(30020, String.format("sorry，Failed to update the %s information(更新%s失败)",extraInfo.getKey(),extraInfo.getKey()));
                    }
                }
            }


            message = Message.ok();

        } catch (Exception e) {
            logger.error("Failed to update StreamisTableMeta: ", e);
            throw new ErrorException(30004,e.getMessage());
        }
        return  Message.messageToResponse(message);
    }

    public Response deleteStreamisTableMeta(HttpServletRequest req,  Long id) throws ErrorException {
        Message message;
        try {
            //delete TableFields
            QueryWrapper<StreamisDatasourceFields> streamisDatasourceFieldsQueryWrapper = new QueryWrapper<>();
            streamisDatasourceFieldsQueryWrapper.eq("streamis_table_meta_id",id);
            boolean remove = streamisDatasourceFieldsService.remove(streamisDatasourceFieldsQueryWrapper);
            if (!remove){
                throw new ErrorException(30008, String.format("Sorry, failed to delete the streamis data source table [%s] field information(抱歉，删除streamis数据源 表[%s]字段信息失败)",id,id));
            }
            QueryWrapper<StreamisDatasourceAuthority> datasourceAuthorityQueryWrapper = new QueryWrapper<>();
            datasourceAuthorityQueryWrapper.eq("streamis_table_meta_id",id);
            remove = streamisDatasourceAuthorityService.remove(datasourceAuthorityQueryWrapper);
            if (!remove){
                throw new ErrorException(30015, String.format("抱歉，删除streamisDatasourceAuthority数据源 表[%s]字段信息失败",id));
            }
            // delete StreamisTableMeta
            boolean result = streamisTableMetaService.removeById(id);
            if (!result){
                throw new ErrorException(30009, String.format("抱歉，删除streamis数据源 表[%s]失败",id));
            }
            message = Message.ok();

        } catch (Exception e) {
            logger.error("Failed to delete StreamisTableMeta: ", e);
            throw new ErrorException(30010, e.getMessage());
        }
        return  Message.messageToResponse(message);
    }


    public Response addStreamisTableFields( HttpServletRequest req,  JsonNode json) throws ErrorException {
        Message message;
        try {
            StreamisDatasourceFields streamisDatasourceFields = mapper.readValue(json.get("streamisTableFields"), StreamisDatasourceFields.class);
            boolean result = streamisDatasourceFieldsService.save(streamisDatasourceFields);
            if(!result){
                throw new ErrorException(30005, "抱歉，添加StreamisTableFields表字段信息失败");
            }
            message = Message.ok();
        } catch (Exception e) {
            logger.error("Failed to add streamisDatasourceFields: ", e);
            throw new ErrorException(30005, e.getMessage());
        }
        return  Message.messageToResponse(message);
    }

    public Response updateStreamisTableFields( HttpServletRequest req,  JsonNode json) throws ErrorException {
        Message message;
        try {
            StreamisDatasourceFields streamisDatasourceFields = mapper.readValue(json.get("streamisTableFields"), StreamisDatasourceFields.class);
            streamisDatasourceFieldsService.updateById(streamisDatasourceFields);
            message = Message.ok();
        } catch (Exception e) {
            logger.error("Failed to update streamisDatasourceFields: ", e);
            throw new ErrorException(30006, "抱歉，更新StreamisTableFields表字段信息失败");
        }
        return  Message.messageToResponse(message);
    }

    public Response deleteStreamisTableFields( HttpServletRequest req, Long id) throws ErrorException {
        Message message;
        String userName = SecurityFilter.getLoginUsername(req);
        try {
            logger.info(userName+" delete StreamisTableFields："+id);
            streamisDatasourceFieldsService.removeById(id);
            message = Message.ok();
        } catch (Exception e) {
            logger.error("Failed to delete StreamisTableFields: ", e);
            throw new ErrorException(30007, "抱歉，删除StreamisTableFields表字段信息失败");
        }
        return  Message.messageToResponse(message);
    }


    /**
     * 获取数据源类型
     * @return 数据源类型列表
     */
    public List<DataSourceType> getDataSourceTypes(String userName){
        return LinkisDataSourceClient.queryDataSourceTypes(userName);
    }

    /**
     *
     * @param system system
     * @param name username
     * @param typeId  typeId
     * @param user hadoop
     * @return 获取数据源集群
     */
    public List<DataSource> getDataSourceClusters(String system,String name,Long typeId ,String user){
        return LinkisDataSourceClient.queryClusterByDataSourceType(system,name,typeId,user);
    }

    /**
     * 获取数据库列表
     * @param system system
     * @param dataSourceId dataSourceId
     * @param user hadoop
     * @return list<String>
     */
    public List<String> queryDataBasesByCuster(String system, Long dataSourceId , String user){
        return LinkisDataSourceClient.queryDataBasesByCuster(system,dataSourceId,user).dbs();
    }

    /**
     * 获取table列表
     * @param system system
     * @param dataSourceId dataSourceId
     * @param dataBase dataBase
     * @param user user
     * @return      * @return
     */
    public List<String> queryTablesByDataBase(String system, Long dataSourceId , String dataBase,String user){
        return LinkisDataSourceClient.queryTablesByDataBase(system,dataSourceId,dataBase ,user).tables();
    }

    public List<MetaColumnInfo> queryColumnsByTable(String system, Long dataSourceId , String dataBase, String table, String user){
        return  LinkisDataSourceClient.queryColumnsByTable(system, dataSourceId, dataBase, table, user).getAllColumns();
    }



    public List<Long> deleteStreamisFields(List<StreamisDatasourceFields> fieldsList,Long streamisTableMetaId){
        QueryWrapper<StreamisDatasourceFields> streamisDatasourceFieldsWrapper = new QueryWrapper<>();
        streamisDatasourceFieldsWrapper.eq("streamis_table_meta_id",streamisTableMetaId);
        List<StreamisDatasourceFields> streamisDatasourceFields = streamisDatasourceFieldsService.list(streamisDatasourceFieldsWrapper);
        List<Long> existsFields = streamisDatasourceFields.stream().map(StreamisDatasourceFields::getId).collect(Collectors.toList());
        List<Long> targetFileds = fieldsList.stream().filter(obj -> obj.getId() != null).map(StreamisDatasourceFields::getId).collect(Collectors.toList());
        //筛选出需要删除的字段id
         existsFields.removeAll(targetFileds);
         return existsFields;
    }
    public List<StreamisDatasourceFields> updateStreamisFields(List<StreamisDatasourceFields> fieldsList,Long streamisTableMetaId){
        List<StreamisDatasourceFields> updateList = new ArrayList<>();
        QueryWrapper<StreamisDatasourceFields> streamisDatasourceFieldsWrapper = new QueryWrapper<>();
        streamisDatasourceFieldsWrapper.eq("streamis_table_meta_id",streamisTableMetaId);
        List<StreamisDatasourceFields> streamisDatasourceFields = streamisDatasourceFieldsService.list(streamisDatasourceFieldsWrapper);
        //过滤新增的记录
        List<StreamisDatasourceFields> filterList = fieldsList.stream().filter(obj -> obj.getId() != null).collect(Collectors.toList());
        //与数据库中字段信息比对，如果存在值不相等，则页面有过修改，保存
        for (StreamisDatasourceFields targetField : filterList) {
            for (StreamisDatasourceFields savedField : streamisDatasourceFields) {
                if(targetField.getId().equals(savedField.getId())){
                    if(!targetField.equals(savedField)){
                        updateList.add(targetField);
                    }
                }
            }
        }
        return updateList;
    }
    public List<StreamisDatasourceFields> insertStreamisFields(List<StreamisDatasourceFields> fieldsList,Long streamisTableMetaId){

        List<StreamisDatasourceFields> collect = fieldsList.stream().filter(obj -> obj.getId() == null).collect(Collectors.toList());
        collect.forEach(obj->obj.setStreamisTableMetaId(streamisTableMetaId));
        return  collect;
    }

    public List<StreamisDatasourceExtraInfo> updateExtraInfo(List<StreamisDatasourceExtraInfo> extraInfoList,Long streamisTableMetaId){
        List<StreamisDatasourceExtraInfo> updateList = new ArrayList<>();
        QueryWrapper<StreamisDatasourceExtraInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("streamis_table_meta_id",streamisTableMetaId);
        List<StreamisDatasourceExtraInfo> targetExtraInfoList = streamisDatasourceExtraInfoService.list(wrapper);
        //过滤新增的记录
        List<StreamisDatasourceExtraInfo> filterList = extraInfoList.stream().filter(obj -> obj.getId() != null).collect(Collectors.toList());
        //与数据库中字段信息比对，如果存在值不相等，则页面有过修改，保存
        for (StreamisDatasourceExtraInfo extraInfo : filterList) {
            for (StreamisDatasourceExtraInfo targetExtraInfo : targetExtraInfoList) {
                if(extraInfo.getId().equals(targetExtraInfo.getId())){
                    if(!targetExtraInfo.equals(extraInfo)){
                        updateList.add(extraInfo);
                    }
                }
            }
        }

        return updateList;
    }


}