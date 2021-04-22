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


import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.streamis.datasource.manager.domain.StreamisDatasourceFields;
import com.webank.wedatasphere.streamis.datasource.manager.domain.StreamisTableMeta;
import com.webank.wedatasphere.streamis.datasource.manager.service.StreamisDatasourceFieldsService;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import com.webank.wedatasphere.streamis.datasource.manager.service.StreamisTableMetaService;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.*;

@Service
public class StreamisTableMetaApi {

    private static final Logger logger = LoggerFactory.getLogger(StreamisTableMetaApi.class);

    @Autowired
    private StreamisTableMetaService streamisTableMetaService;

    @Autowired
    private StreamisDatasourceFieldsService streamisDatasourceFieldsService;
    ObjectMapper mapper = new ObjectMapper();


    /**
     * 获取左侧数据源树，需要调用API获取linkis数据源，还需获取streamis数据源
     */
    public Response getStreamisDataSourceList( HttpServletRequest req, Map<String,Object> json
    ) throws ErrorException {
        Message message = null;
        try{
            String userName = SecurityFilter.getLoginUsername(req);
            String workSpaceName = (String) json.get("workSpaceName");
            String projectName = (String) json.get("projectName");
            String task = (String) json.get("task");
            String dataSourceType = (String) json.get("dataSourceType");
            //TODO: invoke rpc interface to get linkisDataSource Information


            StreamisTableMeta streamisTableMeta = streamisTableMetaService.getById(1);
            logger.warn(streamisTableMeta.toString());
            QueryWrapper<StreamisTableMeta> wrapper = new QueryWrapper<>();
            wrapper.eq("id",1);
            StreamisTableMeta one = streamisTableMetaService.getOne(wrapper);
        }catch (Throwable e){
            logger.error("",e);
            throw new ErrorException(30001, "抱歉，后台获取数据源列表失败");
        }
        return Message.messageToResponse(message);

    }


    public Response getStreamisTableMeta( HttpServletRequest req, Long streamisTableMetaId
                                     ) throws ErrorException {
        Message message = null;
        try{
            StreamisTableMeta streamisTableMeta = streamisTableMetaService.getById(streamisTableMetaId);
            QueryWrapper<StreamisDatasourceFields> streamisDatasourceFieldsWrapper = new QueryWrapper<>();
            streamisDatasourceFieldsWrapper.eq("streamis_table_meta_id",streamisTableMetaId);
            List<StreamisDatasourceFields> streamisDatasourceFields = streamisDatasourceFieldsService.list(streamisDatasourceFieldsWrapper);
            message=Message.ok().data("streamisTableMeta",streamisTableMeta).data("streamisDatasourceFields",streamisDatasourceFields);
        }catch (Throwable e){
            logger.error("Failed to get streamisTableMetaInfo",e);
            throw new ErrorException(30002, "抱歉，后台获取数据源详细信息失败");
        }
        return Message.messageToResponse(message);

    }

    public Response getStreamisTableMetaByNodeNames( HttpServletRequest req,JsonNode json
    ) throws ErrorException {
        Message message = null;
        QueryWrapper<StreamisTableMeta> wrapper = null;
        Map<String,List<StreamisTableMeta>> nodeTablesMap = new HashMap<>();
        List<StreamisTableMeta> list = null;
        try{
            List<String> nodeNames = mapper.readValue(json.get("nodeNames"), new TypeReference<List<String>>() {});

            for (String nodeName : nodeNames) {
                wrapper = new QueryWrapper<>();
                wrapper.eq("node_name",nodeName);
                list = streamisTableMetaService.list(wrapper);
                nodeTablesMap.put(nodeName,list);
            }
            message = Message.ok().data("nodeTables", nodeTablesMap);

        }catch (Throwable e){
            logger.error("Failed to get streamisDataSource Tree",e);
            throw new ErrorException(30011, "抱歉，后台获取左侧数据源树失败");
        }
        return Message.messageToResponse(message);

    }


    public Response addStreamisTableMeta( HttpServletRequest req,  JsonNode json) throws ErrorException {
        Message message = null;
        try {
            String userName = SecurityFilter.getLoginUsername(req);
            StreamisTableMeta streamisTableMeta = mapper.readValue(json.get("streamisTableMeta"), StreamisTableMeta.class);
            //第一次保存linkisDataSource数据源
            streamisTableMeta.setCreateBy(userName);
            boolean result = streamisTableMetaService.save(streamisTableMeta);
            if(!result){
                throw new ErrorException(30003, "抱歉，添加streamis数据源失败");
            }
            Long streamisTableMetaId = streamisTableMeta.getId();

            List<StreamisDatasourceFields> fieldsList = mapper.readValue(json.get("streamisTableFields"), new TypeReference<List<StreamisDatasourceFields>>() {
            });

            fieldsList.forEach(obj ->obj.setStreamisTableMetaId(streamisTableMetaId));
            boolean b = streamisDatasourceFieldsService.saveBatch(fieldsList);
            message = Message.ok().data("streamisTableMetaId",streamisTableMetaId);
        } catch (Exception e) {
            logger.error("Failed to add StreamisTableMeta: ", e);
            throw new ErrorException(30003, "抱歉，添加streamis数据源失败");
        }
        return  Message.messageToResponse(message);
    }


    public Response updateStreamisTableMeta( HttpServletRequest req,  JsonNode json) throws ErrorException {
        Message message = null;
        try {
            String userName = SecurityFilter.getLoginUsername(req);
            StreamisTableMeta streamisTableMeta = mapper.readValue(json.get("streamisTableMeta"), StreamisTableMeta.class);
            Long streamisTableMetaId = streamisTableMeta.getId();
            if(null == streamisTableMetaId ){
                throw new ErrorException(30004, "抱歉，更新streamis数据源信息失败，streamisTableMetaId is null");
            }
            streamisTableMeta.setUpdateBy(userName);
            // update StreamisTableMeta
            streamisTableMetaService.updateById(streamisTableMeta);
            message = Message.ok();

        } catch (Exception e) {
            logger.error("Failed to update StreamisTableMeta: ", e);
            throw new ErrorException(30004, "抱歉，更新streamis数据源信息失败");
        }
        return  Message.messageToResponse(message);
    }

    public Response deleteStreamisTableMeta(HttpServletRequest req,  Long id) throws ErrorException {
        Message message = null;
        try {
            //delete TableFields
            QueryWrapper<StreamisDatasourceFields> streamisDatasourceFieldsQueryWrapper = new QueryWrapper<>();
            streamisDatasourceFieldsQueryWrapper.eq("streamis_table_meta_id",id);
            boolean remove = streamisDatasourceFieldsService.remove(streamisDatasourceFieldsQueryWrapper);
            if (!remove){
                throw new ErrorException(30008, String.format("抱歉，删除streamis数据源 表[%s]字段信息失败",id));
            }
            // delete StreamisTableMeta
            boolean result = streamisDatasourceFieldsService.removeById(id);
            if (!result){
                throw new ErrorException(30009, String.format("抱歉，删除streamis数据源 表[%s]失败",id));
            }
            message = Message.ok();

        } catch (Exception e) {
            logger.error("Failed to delete StreamisTableMeta: ", e);
            throw new ErrorException(30010, "抱歉，删除streamis数据源信息失败");
        }
        return  Message.messageToResponse(message);
    }


    public Response addStreamisTableFields( HttpServletRequest req,  JsonNode json) throws ErrorException {
        Message message = null;
        try {
            StreamisDatasourceFields streamisDatasourceFields = mapper.readValue(json.get("streamisTableFields"), StreamisDatasourceFields.class);
            boolean result = streamisDatasourceFieldsService.save(streamisDatasourceFields);
            if(!result){
                throw new ErrorException(30005, "抱歉，添加StreamisTableFields表字段信息失败");
            }
            message = Message.ok();
        } catch (Exception e) {
            logger.error("Failed to add streamisDatasourceFields: ", e);
            throw new ErrorException(30005, "抱歉，添加StreamisTableFields表字段信息失败");
        }
        return  Message.messageToResponse(message);
    }

    public Response updateStreamisTableFields( HttpServletRequest req,  JsonNode json) throws ErrorException {
        Message message = null;
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
        Message message = null;
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


}
