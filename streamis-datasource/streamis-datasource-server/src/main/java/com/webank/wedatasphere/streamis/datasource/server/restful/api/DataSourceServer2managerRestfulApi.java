package com.webank.wedatasphere.streamis.datasource.server.restful.api;

import com.webank.wedatasphere.linkis.common.exception.ErrorException;
import com.webank.wedatasphere.streamis.datasource.manager.restful.api.StreamisTableMetaApi;
import org.codehaus.jackson.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Map;

@Component
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("streamis")
public class DataSourceServer2managerRestfulApi {

    @Autowired
    private StreamisTableMetaApi streamisTableMetaApi;


    @POST
    @Path("dataSourceType")
    public Response getDataSourceType(@Context HttpServletRequest req, Map<String,Object> json
    ) throws ErrorException {
        return streamisTableMetaApi.getDataSourceTypes(req, json);
    }

    @POST
    @Path("dataSourceCluster")
    public Response getDataSourceCluster(@Context HttpServletRequest req, Map<String,String> json
    ) throws ErrorException {
        return streamisTableMetaApi.getDataSourceCluster(req,json);
    }

    @POST
    @Path("dataBases")
    public Response getDataBasesByCuster(@Context HttpServletRequest req, Map<String,String> json
    ) throws ErrorException {
        return streamisTableMetaApi.getDataBasesByCuster(req,json);
    }

    @POST
    @Path("tables")
    public Response getTablesByDataBase(@Context HttpServletRequest req, Map<String,String> json
    ) throws ErrorException {
        return streamisTableMetaApi.getTablesByDataBase(req,json);
    }

    @POST
    @Path("columns")
    public Response getColumnsByTable(@Context HttpServletRequest req, Map<String,String> json
    ) throws ErrorException {
        return streamisTableMetaApi.getColumnsByTable(req,json);
    }

    /**
     * 获取数据源的详细信息，包括数据源信息、表信息和字段信息
     */
    @GET
    @Path("streamisTableMetaInfo/{streamis_table_meta_Id}")
    public Response getStreamisTableMeta(@Context HttpServletRequest req,  @PathParam("streamis_table_meta_Id")Long streamisTableMetaId
    ) throws ErrorException {
        return  streamisTableMetaApi.getStreamisTableMeta(req,streamisTableMetaId);
    }

    @POST
    @Path("save")
    public Response saveStreamisTableMeta( @Context HttpServletRequest req,  JsonNode json) throws ErrorException{
        return  streamisTableMetaApi.saveStreamisTableMeta(req, json);
    }

    @POST
    @Path("streamisDataSourceTree")
    public Response getStreamisTableMetaByNodeNames(@Context HttpServletRequest req,JsonNode json
    ) throws ErrorException {
       return streamisTableMetaApi.getStreamisTableMetaByNodeNames(req,json);
    }

    @POST
    @Path("addStreamisTableMeta")
    public Response addStreamisTableMeta(@Context HttpServletRequest req,  JsonNode json) throws ErrorException {
       return streamisTableMetaApi.addStreamisTableMeta(req,json);
    }

    @POST
    @Path("updateStreamisTableMeta")
    public Response updateStreamisTableMeta(@Context HttpServletRequest req,  JsonNode json) throws ErrorException {
        return  streamisTableMetaApi.updateStreamisTableMeta(req, json);

    }

    @POST
    @Path("streamisTableMetaInfo/{streamis_table_meta_Id}")
    public Response deleteStreamisTableMeta(@Context  HttpServletRequest req,  @PathParam("streamis_table_meta_Id")Long id) throws ErrorException {
        return streamisTableMetaApi.deleteStreamisTableMeta(req,id);
    }

    @POST
    @Path("addFields")
    public Response addStreamisTableFields(@Context HttpServletRequest req,  JsonNode json) throws ErrorException {
       return streamisTableMetaApi.addStreamisTableFields(req,json);
    }

    @POST
    @Path("updateFields")
    public Response updateStreamisTableFields(@Context HttpServletRequest req,  JsonNode json) throws ErrorException {
       return streamisTableMetaApi.updateStreamisTableFields(req, json);
    }
    @GET
    @Path("deleteFields/{id}")
    public Response deleteStreamisTableFields(@Context HttpServletRequest req,  @PathParam("id") Long id) throws ErrorException {
       return streamisTableMetaApi.deleteStreamisTableFields(req,id);
    }
}
