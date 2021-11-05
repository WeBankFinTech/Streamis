/*
 * Copyright 2021 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

package com.webank.wedatasphere.streamis.jobmanager.restful.api;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.linkis.server.Message;
import com.webank.wedatasphere.linkis.server.security.SecurityFilter;
import com.webank.wedatasphere.streamis.jobmanager.exception.JobException;
import com.webank.wedatasphere.streamis.jobmanager.exception.JobExceptionManager;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.MetaJsonInfo;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJobVersion;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.JobDetailsVO;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.JobProgressVO;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.QueryJobListVO;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.StreamTaskListVO;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.VersionDetailVO;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.JobService;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.TaskService;
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity.StreamisTransformJobContent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@Path("/streamis/streamJobManager/job")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JobRestfulApi {
    @Autowired
    JobService jobService;
    @Autowired
    TaskService taskService;

    @GET
    @Path("/list")
    public Response getJobList(@Context HttpServletRequest req,
                               @QueryParam("pageNow") Integer pageNow,
                               @QueryParam("pageSize") Integer pageSize,
                               @QueryParam("projectName") String projectName,
                               @QueryParam("jobName") String jobName,
                               @QueryParam("jobStatus") Integer jobStatus,
                               @QueryParam("jobCreator") String jobCreator) {
        String username = SecurityFilter.getLoginUsername(req);
        if (StringUtils.isEmpty(pageNow)) {
            pageNow = 1;
        }
        if (StringUtils.isEmpty(pageSize)) {
            pageSize = 20;
        }
        PageInfo<QueryJobListVO> pageInfo;
        PageHelper.startPage(pageNow, pageSize);
        try {
            pageInfo = jobService.getByProList(projectName, jobName, jobStatus, jobCreator);
        } finally {
            PageHelper.clearPage();
        }

        return Message.messageToResponse(Message.ok().data("tasks", pageInfo.getList()).data("totalPage", pageInfo.getTotal()));
    }

    @POST
    @Path("/createOrUpdate")
    public Response createOrUpdate(@Context HttpServletRequest req,@RequestBody MetaJsonInfo metaJsonInfo) throws Exception {
        String username = SecurityFilter.getLoginUsername(req);
        if (org.apache.commons.lang.StringUtils.isBlank(metaJsonInfo.getJobName())) {
            return Message.messageToResponse(Message.error("jobName is null"));
        }
        if (org.apache.commons.lang.StringUtils.isBlank(metaJsonInfo.getJobType())) {
            return Message.messageToResponse(Message.error("jobType is null"));
        }
        if (org.apache.commons.lang.StringUtils.isBlank(metaJsonInfo.getProjectName())) {
            return Message.messageToResponse(Message.error("projectName is null"));
        }
        StreamJobVersion job = jobService.createOrUpdate(username, metaJsonInfo);
        return Message.messageToResponse(Message.ok().data("jobId",job.getJobId()));
    }

    @GET
    @Path("/version")
    public Response version(@QueryParam("jobId") Long jobId, @QueryParam("version") String version) throws JobException {
        if (jobId == null) {
            JobExceptionManager.createException(30301, "jobId");
        }
        if (StringUtils.isEmpty(version)) {
            JobExceptionManager.createException(30301, "version");
        }
        VersionDetailVO versionDetailVO = jobService.versionDetail(jobId, version);
        return Message.messageToResponse(Message.ok().data("detail", versionDetailVO));
    }


    @POST
    @Path("/execute")
    public Response executeJob(@Context HttpServletRequest req, Map<String, Object> json) throws JobException {
        String userName = SecurityFilter.getLoginUsername(req);
        if (!json.containsKey("jobId") || json.get("jobId") == null) {
            JobExceptionManager.createException(30301, "jobId");
        }
        long jobId = Long.parseLong(json.get("jobId").toString());
        taskService.executeJob(jobId, userName);
        return Message.messageToResponse(Message.ok());
    }

    @GET
    @Path("/stop")
    public Response killJob(@Context HttpServletRequest req, @QueryParam("jobId") Long jobId) throws JobException {
        String userName = SecurityFilter.getLoginUsername(req);
        if (jobId == null) {
            JobExceptionManager.createException(30301, "jobId");
        }
        taskService.stopJob(jobId, userName);
        return Message.messageToResponse(Message.ok());
    }

    @GET
    @Path("/details")
    public Response detailsJob(@Context HttpServletRequest req, @QueryParam("jobId") Long jobId) throws IOException, JobException {
        if (jobId == null) {
            JobExceptionManager.createException(30301, "jobId");
        }
        JobDetailsVO jobDetailsVO = new JobDetailsVO();
        List<JobDetailsVO.DataNumberDTO> dataNumberDTOS = new ArrayList<>();
        JobDetailsVO.DataNumberDTO dataNumberDTO = new JobDetailsVO.DataNumberDTO();
        dataNumberDTO.setDataName("kafka topic");
        dataNumberDTO.setDataNumber(109345);
        dataNumberDTOS.add(dataNumberDTO);

        List<JobDetailsVO.LoadConditionDTO> loadConditionDTOs = new ArrayList<>();
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

        return Message.messageToResponse(Message.ok().data("details", jobDetailsVO));
    }

    @GET
    @Path("/execute/history")
    public Response executeHistoryJob(@Context HttpServletRequest req, @QueryParam("jobId") Long jobId, @QueryParam("version") String version) throws IOException, JobException {
        if (jobId == null) {
            JobExceptionManager.createException(30301, "jobId");
        }
        if (StringUtils.isEmpty(version)) {
            JobExceptionManager.createException(30301, "version");
        }
        List<StreamTaskListVO> details = taskService.executeHistory(jobId, version);
        return Message.messageToResponse(Message.ok().data("details", details));
    }


    @GET
    @Path("/progress")
    public Response progressJob(@Context HttpServletRequest req, @QueryParam("jobId") Long jobId,@QueryParam("version") String version) throws IOException, JobException {
        if (jobId == null) {
            JobExceptionManager.createException(30301, "jobId");
        }
        JobProgressVO jobProgressVO = taskService.getByJobStatus(jobId,version);
        return Message.messageToResponse(Message.ok().data("taskId", jobProgressVO.getTaskId()).data("progress", jobProgressVO.getProgress()));
    }

    @GET
    @Path("/jobContent")
    public Response uploadDetailsJob(@Context HttpServletRequest req, @QueryParam("jobId") Long jobId,
        @QueryParam("version") String version) {
        StreamisTransformJobContent jobContent = jobService.getJobContent(jobId, version);
        return Message.messageToResponse(Message.ok().data("jobContent", jobContent));
    }

    @GET
    @Path("/back")
    public Response back(@QueryParam("jobId") Long jobId) throws JobException {
        if (jobId == null) {
            JobExceptionManager.createException(30301, "jobId");
        }
        String url = "http://bdphdp110002:8088/proxy/application_1623431123087_43873/#/overview";
        return Message.messageToResponse(Message.ok().data("url", url));
    }
}
