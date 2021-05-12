package com.webank.wedatasphere.streamis.jobmanager.restful.api;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import com.webank.wedatasphere.streamis.jobmanager.exception.JobException;
import com.webank.wedatasphere.streamis.jobmanager.exception.JobExceptionManager;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.*;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.JobService;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.ProjectService;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.TaskService;
import org.codehaus.jackson.map.ObjectMapper;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Path("/streamis/streamJobManager/job")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JobRestfulApi {
    @Autowired
    JobService jobService;
    @Autowired
    TaskService taskService;
    @Autowired
    ProjectService projectService;

    ObjectMapper mapper = new ObjectMapper();

    @GET
    @Path("/list")
    public Response getJobList(@Context HttpServletRequest req,
                               @QueryParam("projectId") Long projectId,
                               @QueryParam("workspaceId") Long workspaceId,
                               @QueryParam("pageNow") Integer pageNow,
                               @QueryParam("pageSize") Integer pageSize,
                               @QueryParam("jobName") String jobName,
                               @QueryParam("jobStatus") Integer jobStatus,
                               @QueryParam("jobCreator") String jobCreator) throws IOException, JobException {
        if (StringUtils.isEmpty(pageNow)) {
            pageNow = 1;
        }
        if (StringUtils.isEmpty(pageSize)) {
            pageSize = 20;
        }
        if(projectId == null){
            JobExceptionManager.createException(30301,"projectId");
        }
        List<QueryJobListVO> jobList = null;
        PageHelper.startPage(pageNow, pageSize);
        try {
            jobList = jobService.getByProList(projectId,jobName,jobStatus,jobCreator);
        }finally {
            PageHelper.clearPage();
        }
        PageInfo<QueryJobListVO> pageInfo = new PageInfo<>(jobList);
        long total = pageInfo.getTotal();

        return Message.messageToResponse(Message.ok().data("tasks", pageInfo.getList()).data("totalPage", total));
    }

    @POST
    @Path("/upload")
    public Response uploadJar(@Context HttpServletRequest req,
                           @FormDataParam("jobName") String jobName,
                           @FormDataParam("entrypointClass") String entrypointClass,
                           @FormDataParam("label") String label,
                           @FormDataParam("entrypointMainArgs") String entrypointMainArgs,
                           @FormDataParam("parallelism") Integer parallelism,
                           @FormDataParam("workspaceId") Long workspaceId,
                           @FormDataParam("projectId") Long projectId,
                           @FormDataParam("path") String path,
                           FormDataMultiPart form) throws IOException, JobException {

        if (StringUtils.isEmpty(path)) {
            JobExceptionManager.createException(30300,path);
        }

        return Message.messageToResponse(Message.ok());
    }

    @POST
    @Path("/execute")
    public Response executeJob(@Context HttpServletRequest req, Map<String, Object> json) throws IOException, JobException  {
        String userName = SecurityFilter.getLoginUsername(req);
        if(!json.containsKey("jobId") || json.get("jobId")==null){
            JobExceptionManager.createException(30301,"jobId");
        }
        long jobId = Long.parseLong(json.get("jobId").toString());
        taskService.executeJob(jobId,userName);
        //todo 缺少linkis
        return Message.messageToResponse(Message.ok());
    }

    @GET
    @Path("/stop")
    public Response killJob(@Context HttpServletRequest req,@QueryParam("jobId") Long jobId) throws IOException, JobException{
        String userName = SecurityFilter.getLoginUsername(req);
        if(jobId == null){
            JobExceptionManager.createException(30301,"jobId");
        }
        taskService.stopJob(jobId,userName);
        //todo 缺少linkis
        return Message.messageToResponse(Message.ok());
    }

    @GET
    @Path("/details")
    public Response detailsJob(@Context HttpServletRequest req,@QueryParam("jobId") Long jobId) throws IOException, JobException{
        if(jobId == null){
            JobExceptionManager.createException(30301,"jobId");
        }
        JobDetailsVO jobDetailsVO = new JobDetailsVO();
        List<JobDetailsVO.DataNumberDTO> dataNumberDTOS =new ArrayList<>();
        JobDetailsVO.DataNumberDTO dataNumberDTO = new JobDetailsVO.DataNumberDTO();
        dataNumberDTO.setDataName("kafka topic");
        dataNumberDTO.setDataNumber(109345);
        dataNumberDTOS.add(dataNumberDTO);

        List<JobDetailsVO.LoadConditionDTO> loadConditionDTOs =new ArrayList<>();
        JobDetailsVO.LoadConditionDTO loadConditionDTO = new JobDetailsVO.LoadConditionDTO();
        loadConditionDTO.setType("jobManager");
        loadConditionDTO.setHost("localhost");
        loadConditionDTO.setMemory("1.5");
        loadConditionDTO.setTotalMemory("2.0");
        loadConditionDTO.setGcLastTime("2020-08-01");
        loadConditionDTO.setGcLastConsume("1");
        loadConditionDTO.setGcTotalTime("2min");
        loadConditionDTOs.add(loadConditionDTO);

        List<JobDetailsVO.RealTimeTrafficDTO> realTimeTrafficDTOS = new ArrayList<>();
        JobDetailsVO.RealTimeTrafficDTO realTimeTrafficDTO = new JobDetailsVO.RealTimeTrafficDTO();
        realTimeTrafficDTO.setSourceKey("kafka topic");
        realTimeTrafficDTO.setSourceSpeed("100 Records/S");
        realTimeTrafficDTO.setTransformKey("transform");
        realTimeTrafficDTO.setSinkKey("hbase key");
        realTimeTrafficDTO.setSinkSpeed("10 Records/S");
        realTimeTrafficDTOS.add(realTimeTrafficDTO);

        jobDetailsVO.setDataNumber(dataNumberDTOS);
        jobDetailsVO.setLoadCondition(loadConditionDTOs);
        jobDetailsVO.setRealTimeTraffic(realTimeTrafficDTOS);

        return Message.messageToResponse(Message.ok().data("details",jobDetailsVO));
    }
    @GET
    @Path("/execute/history")
    public Response executeHistoryJob(@Context HttpServletRequest req,@QueryParam("jobId") Long jobId,@QueryParam("version") String version) throws IOException, JobException{
        if(jobId == null){
            JobExceptionManager.createException(30301,"jobId");
        }
        if(StringUtils.isEmpty(version)){
            JobExceptionManager.createException(30301,"version");
        }
        List<StreamTaskListVO> streamTaskListVOS = taskService.executeHistory(jobId, version);
        return Message.messageToResponse(Message.ok().data("details",streamTaskListVOS));
    }

    @POST
    @Path("/release")
    public Response releaseJob(@Context HttpServletRequest req, Map<String, Object> json) throws IOException, JobException{

        return Message.messageToResponse(Message.ok());
    }
    @POST
    @Path("/publishToJobManager")
    public Response publishToJobManager(@Context HttpServletRequest req, Map<String, Object> json) throws IOException, JobException{
        if(json == null){
            JobExceptionManager.createException(30301,"version");
        }
        PublishRequestVO publishRequestVO = mapper.readValue(mapper.writeValueAsString(json), PublishRequestVO.class);
        if(publishRequestVO == null){
            JobExceptionManager.createException(30301,"version");
        }


        //todo 发布
        return Message.messageToResponse(Message.ok());
    }

    @GET
    @Path("/flow")
    public Response flowJob(@Context HttpServletRequest req,@QueryParam("jobId") Long jobId,@QueryParam("version") String version) throws IOException, JobException{
        if(jobId == null){
            JobExceptionManager.createException(30301,"jobId");
        }
        if(StringUtils.isEmpty(version)){
            JobExceptionManager.createException(30301,"version");
        }

        JobFlowVO jobFlow = jobService.getJobFlow(jobId, version);

        return Message.messageToResponse(Message.ok().data("flowData",jobFlow));
    }
    @GET
    @Path("/progress")
    public Response progressJob(@Context HttpServletRequest req,@QueryParam("jobId") Long jobId) throws IOException, JobException{
        if(jobId == null){
            JobExceptionManager.createException(30301,"jobId");
        }
        JobProgressVO jobProgressVO = taskService.getByJobStatus(jobId);
        return Message.messageToResponse(Message.ok().data("taskId",jobProgressVO.getTaskId()).data("progress",jobProgressVO.getProgress()));
    }

    @GET
    @Path("/upload/details")
    public Response uploadDetailsJob(@Context HttpServletRequest req,@QueryParam("jobId") Long jobId) throws IOException, JobException{

        JobUploadDetailsVO jobUploadDetailsVO = new JobUploadDetailsVO();
        List<JobUploadDetailsVO.JarDependentDTO> mainJars = new ArrayList<>();
        List<JobUploadDetailsVO.JarDependentDTO> dependentList = new ArrayList<>();
        List<JobUploadDetailsVO.JarDependentDTO> userList = new ArrayList<>();
        JobUploadDetailsVO.JarDependentDTO jarDependentDTO = new JobUploadDetailsVO.JarDependentDTO();
        jarDependentDTO.setId(1L);
        jarDependentDTO.setName("main jar");

        mainJars.add(jarDependentDTO);

        jarDependentDTO = new JobUploadDetailsVO.JarDependentDTO();
        jarDependentDTO.setId(1L);
        jarDependentDTO.setName("jar 依赖");
        dependentList.add(jarDependentDTO);

        jarDependentDTO = new JobUploadDetailsVO.JarDependentDTO();
        jarDependentDTO.setId(1L);
        jarDependentDTO.setName("user 资源");
        userList.add(jarDependentDTO);

        jobUploadDetailsVO.setProgramArguement("--hostname localhost  --port 12345");
        jobUploadDetailsVO.setMainJars(mainJars);
        jobUploadDetailsVO.setDependentList(dependentList);
        jobUploadDetailsVO.setUserList(userList);

        return Message.messageToResponse(Message.ok().data("details",jobUploadDetailsVO));
    }

}
