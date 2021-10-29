package com.webank.wedatasphere.streamis.datasource.server.restful.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.webank.wedatasphere.linkis.bml.Entity.Resource;
import com.webank.wedatasphere.linkis.bml.Entity.ResourceVersion;
import com.webank.wedatasphere.linkis.bml.common.*;
import com.webank.wedatasphere.linkis.bml.dao.ResourceDao;
import com.webank.wedatasphere.linkis.bml.dao.VersionDao;
import com.webank.wedatasphere.linkis.bml.restful.RestfulUtils;
import com.webank.wedatasphere.linkis.bml.service.ResourceService;
import com.webank.wedatasphere.linkis.bml.service.VersionService;
import com.webank.wedatasphere.linkis.bml.util.HttpRequestHelper;
import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.common.io.Fs;
import com.webank.wedatasphere.linkis.common.io.FsPath;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import com.webank.wedatasphere.linkis.storage.FSFactory;
import com.webank.wedatasphere.streamis.datasource.manager.domain.StreamisDatasourceExtraInfo;
import com.webank.wedatasphere.streamis.datasource.manager.domain.StreamisDatasourceFields;
import com.webank.wedatasphere.streamis.datasource.manager.domain.StreamisTableMeta;
import com.webank.wedatasphere.streamis.datasource.manager.service.StreamisDatasourceFieldsService;
import com.webank.wedatasphere.streamis.datasource.manager.service.StreamisTableMetaService;
import com.webank.wedatasphere.streamis.datasource.transfer.entity.StreamisDataSourceCode;
import com.webank.wedatasphere.streamis.datasource.transfer.entity.StreamisTableEntity;
import com.webank.wedatasphere.streamis.datasource.transfer.service.DataSourceTransfer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.*;

@Component
@Path("streamis")
public class DataSourceTransferRestfulApi {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceTransferRestfulApi.class);

    @Autowired
    private DataSourceTransfer dataSourceTransfer;

    @Autowired
    private StreamisTableMetaService streamisTableMetaService;
    @Autowired
    private StreamisDatasourceFieldsService streamisDatasourceFieldsService;

    @Autowired
    private ResourceDao resourceDao;
    @Autowired
    private VersionDao versionDao;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private VersionService versionService;
    ObjectMapper mapper = new ObjectMapper();

    @POST
    @Path("/transfer")
    public Response transfer(@Context HttpServletRequest request, JsonNode json) throws ErrorException, IOException {
        Message message;
        String streamisTableMetaId =json.get("streamisTableMetaId").asText();
        String dataSource =json.get("dataSourceId").asText();
        String nodeName =json.get("nodeName").asText();
//        Map<String, Object> labels = mapper.readValue(json.get("streamisExtraInfo"), new TypeReference<Map<String, Object>>() {
//        });
        List<StreamisDatasourceExtraInfo> extraInfoList = mapper.readValue(json.get("labels"), new TypeReference<List<StreamisDatasourceExtraInfo>>() {
        });

        //预留参数
        Map<String, Object> labels = mapper.readValue(json.get("labels"), new TypeReference<Map<String, Object>>() {
        });
        if(StringUtils.isBlank(streamisTableMetaId)){
            throw new ErrorException(30201,"streamisTableMetaId is null.");
        }
        //@QueryParam("streamisTableMetaId") String streamisTableMetaId, @QueryParam("dataSourceId") String dataSource,@QueryParam("nodeName") String nodeName, @FormDataParam("labels") Map<String, Object> labels
        String userName = SecurityFilter.getLoginUsername(request);
        Map<String,Object> params = new HashMap<>();
        try {

            //TODO: 如何获取runType  engineType  version
            params.put("nodeName",nodeName);
            params.put("runType","sql");
            params.put("engineType","flink");
            params.put("version","1.12.2");
            params.put("extraInfo",extraInfoList);
            Long dataSourceId = Long.parseLong(dataSource);
            //通过streamisTableMetaId拿到StreamisTableMeta实体
            StreamisTableEntity streamisTableEntity = dataSourceTransfer.getStreamisTableMetaById(streamisTableMetaId,userName,dataSourceId);

            //转换为FlinkSql
            StreamisDataSourceCode streamisDataSourceCode = dataSourceTransfer.transfer(streamisTableEntity, labels, params);
            message = Message.ok().data("streamisDataSourceCode",streamisDataSourceCode);
        } catch (Exception e) {
            logger.error("传入streamisTableMetaId {} data source conversion failed：", streamisTableMetaId, e);
            throw new ErrorException(30202, "Sorry, background data source conversion failed(后台数据源转换失败)");
        }

        return Message.messageToResponse(message);
    }

    //上传数据源到hdfs
    @POST
    @Path("/exportStreamisTableMeta")
    public Response exportStreamisTableMeta(@Context HttpServletRequest request, JsonNode jsonNode) throws ErrorException{
        Message message = new Message();
        String user = RestfulUtils.getUserName(request);
        String clientIp = HttpRequestHelper.getIp(request);
        Map<String,Object> streamisEntity = new HashMap<>();
        ResourceHelper resourceHelper = ResourceHelperFactory.getResourceHelper();
        //通过streamisTableMetaId拿到StreamisTableMeta实体
        String streamisTableMetaId = jsonNode.get("streamisTableMetaId").asText();
        StreamisTableMeta streamisTableMeta = streamisTableMetaService.getById(streamisTableMetaId);

        QueryWrapper<StreamisDatasourceFields> streamisDatasourceFieldsWrapper = new QueryWrapper<>();
        streamisDatasourceFieldsWrapper.eq("streamis_table_meta_id",streamisTableMetaId);
        List<StreamisDatasourceFields> streamisDatasourceFields = streamisDatasourceFieldsService.list(streamisDatasourceFieldsWrapper);

        streamisEntity.put("streamisTableMeta",streamisTableMeta);
        streamisEntity.put("streamisDatasourceFields",streamisDatasourceFields);
        Gson gson = new Gson();
        String json = gson.toJson(streamisEntity);

        if(json != null) {
            String resourceId = UUID.randomUUID().toString();
            String fileName = UUID.randomUUID().toString() + ".json";
            String path = resourceHelper.generatePath(user, fileName, new HashMap<String, Object>());
            InputStream in_nocode = new ByteArrayInputStream(json.getBytes());
            StringBuilder sb = new StringBuilder();
            //上传资源到HDFS
            long size = resourceHelper.upload(path, user, in_nocode, sb, true);
            String md5String = sb.toString();
            boolean isSuccess = false;
            if (StringUtils.isNotEmpty(md5String) && size >= 0) {
                isSuccess = true;
            }
            Resource resource = Resource.createNewResource(resourceId, user, fileName, new HashMap<String, Object>());
            //插入一条记录到resource表
            long id = resourceDao.uploadResource(resource);
            logger.info("{} uploaded a resource and resourceId is {}", user, resource.getResourceId());
            //插入一条记录到resource version表
            //TODO  版本是否固定为v000001？
            ResourceVersion resourceVersion = ResourceVersion.createNewResourceVersion(resourceId, path, md5String,
                    clientIp, size, Constant.FIRST_VERSION, 1);
            versionDao.insertNewVersion(resourceVersion);

            message = Message.ok().data("resourceId",resourceId).data("version",resourceVersion.getVersion());
        }

        return Message.messageToResponse(message);
    }

    //从hdfs下载数据源，插入数据库
    @POST
    @Path("/importStreamisTableMeta")
    public Response importStreamisTableMeta(@Context HttpServletRequest request, @Context HttpServletResponse resp,JsonNode jsonNode )
            throws ErrorException, IOException {
        Message message = new Message();
        String user = RestfulUtils.getUserName(request);
        String resourceId =  jsonNode.get("resourceId").asText();
        String version =  jsonNode.get("version").asText();
        if (StringUtils.isBlank(resourceId) || !resourceService.checkResourceId(resourceId)) {
            message = Message.error("resourceId:"+resourceId+"为空,非法或者已被删除!");
            message.setStatus(1);
            return Message.messageToResponse(message);
        }
        if (!resourceService.checkAuthority(user, resourceId)){
            throw new BmlPermissionDeniedException("您没有权限下载此资源");
        }
        //判version空,返回最新版本
        if (StringUtils.isBlank(version)){
            version = versionService.getNewestVersion(resourceId);
        }
        //判version不存在或者非法
        if (!versionService.checkVersion(resourceId, version)) {
            message = Message.error("version:"+version+"不存在,非法或者已被删除");
            message.setStatus(1);
            return Message.messageToResponse(message);
        }
        //判resourceId和version是否过期
        if (!resourceService.checkExpire(resourceId, version)){
            throw new BmlResourceExpiredException(resourceId);
        }

        ResourceVersion resourceVersion = versionDao.findResourceVersion(resourceId, version);
        long startByte = resourceVersion.getStartByte();
        long endByte = resourceVersion.getEndByte();
        String path = resourceVersion.getResource();
        Fs fileSystem = FSFactory.getFsByProxyUser(new FsPath(path), user);
        fileSystem.init(new HashMap());
        InputStream inputStream = fileSystem.read(new FsPath(path));
        inputStream.skip(startByte - 1L);
        logger.info("{} 下载资源 {} inputStream skipped {} bytes", new Object[]{user, resourceId, startByte - 1L});

        try {
            //将流转换为json
            Gson gson = new Gson();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder sb = new StringBuilder();
            String s = null;
            while ((s = bufferedReader.readLine()) != null) {
                sb.append(s);
                sb.append("\n");
            }
            String json = sb.toString().trim();
            //将存放入数组的数据转为map格式
            HashMap map = gson.fromJson(sb.toString(), HashMap.class);
            message.data("streamisEntity",map);
        } catch (IOException e) {
            logger.error("用户 {} 下载资源 resourceId: {}, version:{} 出现IO异常", user, resourceId, version, e);
            throw new ErrorException(73562, "抱歉,后台IO错误造成您本次下载资源失败");
        } catch (final Throwable t){
            logger.error("用户 {} 下载资源 resourceId: {}, version:{} 出现异常", user, resourceId, version, t);
            throw new ErrorException(73561, "抱歉，后台服务出错导致您本次下载资源失败");
        }finally {
            IOUtils.closeQuietly(inputStream);
        }
        return Message.messageToResponse(message);
    }

}
