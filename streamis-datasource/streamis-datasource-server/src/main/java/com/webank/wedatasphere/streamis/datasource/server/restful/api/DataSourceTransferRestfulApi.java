package com.webank.wedatasphere.streamis.datasource.server.restful.api;

import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.streamis.datasource.transfer.entity.StreamisDataSourceCode;
import com.webank.wedatasphere.streamis.datasource.transfer.entity.StreamisTableEntity;
import com.webank.wedatasphere.streamis.datasource.transfer.service.DataSourceTransfer;
import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

public class DataSourceTransferRestfulApi {
    private static final Logger logger = LoggerFactory.getLogger(DataSourceTransferRestfulApi.class);

    @Autowired
    private DataSourceTransfer dataSourceTransfer;

    @GET
    @Path("/transfer")
    public Response transfer(@Context HttpServletRequest request, @QueryParam("streamisTableMetaId") String streamisTableMetaId, @FormDataParam("labels") Map<String, Object> labels) throws ErrorException {
        Message message;
        if(StringUtils.isBlank(streamisTableMetaId)){
            throw new ErrorException(30201,"streamisTableMetaId is null.");
        }
        try {
            //通过streamisTableMetaId拿到StreamisTableMeta实体
            StreamisTableEntity streamisTableEntity = dataSourceTransfer.getStreamisTableMetaById(streamisTableMetaId);
            //转换为FlinkSql
            StreamisDataSourceCode streamisDataSourceCode = dataSourceTransfer.transfer(streamisTableEntity, labels, new HashMap<String, Object>());
            message = Message.ok().data("streamisDataSourceCode",streamisDataSourceCode);
        } catch (Exception e) {
            logger.error("传入streamisTableMetaId {} 数据源转换失败：", streamisTableMetaId, e);
            throw new ErrorException(30202, "抱歉，后台数据源转换失败");
        }

        return Message.messageToResponse(message);
    }

    @POST
    @Path("/exportStreamisTableMeta")
    public Response exportStreamisTableMeta(@Context HttpServletRequest request, @FormDataParam("streamisTableMetaId") String streamisTableMetaId){
        Message message;

        return Message.messageToResponse(Message.ok());
    }

}
