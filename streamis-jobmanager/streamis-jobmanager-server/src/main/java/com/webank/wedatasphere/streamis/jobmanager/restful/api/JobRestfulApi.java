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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.streamis.jobmanager.exception.JobException;
import com.webank.wedatasphere.streamis.jobmanager.exception.JobExceptionManager;
import com.webank.wedatasphere.streamis.jobmanager.launcher.conf.JobConfKeyConstants;
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.JobInfo;
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.JobConf;
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.manager.JobLaunchManager;
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobStateInfo;
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.conf.JobLauncherConfiguration;
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.entity.LogRequestPayload;
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.jobInfo.EngineConnJobInfo;
import com.webank.wedatasphere.streamis.jobmanager.launcher.service.StreamJobConfService;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.MetaJsonInfo;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJob;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJobVersion;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamTask;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.*;
import com.webank.wedatasphere.streamis.jobmanager.manager.project.service.ProjectPrivilegeService;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.StreamJobInspectService;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.StreamJobService;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.StreamTaskService;
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity.RealtimeLogEntity;
import com.webank.wedatasphere.streamis.jobmanager.manager.transform.entity.StreamisTransformJobContent;
import com.webank.wedatasphere.streamis.jobmanager.manager.utils.StreamTaskUtils;
import com.webank.wedatasphere.streamis.jobmanager.service.HighAvailableService;
import com.webank.wedatasphere.streamis.jobmanager.utils.RegularUtil;
import com.webank.wedatasphere.streamis.jobmanager.vo.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.httpclient.dws.DWSHttpClient;
import org.apache.linkis.server.Message;
import org.apache.linkis.server.utils.ModuleUserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequestMapping(path = "/streamis/streamJobManager/job")
@RestController
public class JobRestfulApi {

    private static final Logger LOG = LoggerFactory.getLogger(JobRestfulApi.class);

    @Autowired
    private StreamJobService streamJobService;

    @Autowired
    private StreamJobConfService streamJobConfService;

    @Autowired
    private StreamTaskService streamTaskService;

    @Autowired
    private StreamJobInspectService streamJobInspectService;

    @Autowired
    private HighAvailableService highAvailableService;

    @Resource
    private JobLaunchManager<? extends JobInfo> jobLaunchManager;

    @Resource
    private ProjectPrivilegeService privilegeService;

    @RequestMapping(path = "/list", method = RequestMethod.GET)
    public Message getJobList(HttpServletRequest req,
                              @RequestParam(value = "pageNow", required = false) Integer pageNow,
                              @RequestParam(value = "pageSize", required = false) Integer pageSize,
                              @RequestParam(value = "projectName", required = false) String projectName,
                              @RequestParam(value = "jobName", required = false) String jobName,
                              @RequestParam(value = "jobStatus", required = false) Integer jobStatus,
                              @RequestParam(value = "jobCreator", required = false) String jobCreator,
                              @RequestParam(value = "label", required = false) String label,
                              @RequestParam(value = "enable", required = false) Boolean enable,
                              @RequestParam(value = "jobType", required = false) String jobType) {
        String username = ModuleUserUtils.getOperationUser(req, "list jobs");
        if(StringUtils.isBlank(projectName)){
            return Message.error("Project name cannot be empty(项目名不能为空，请指定)");
        }
        if (Objects.isNull(pageNow)) {
            pageNow = 1;
        }
        if (Objects.isNull(pageSize)) {
            pageSize = 20;
        }
        PageInfo<QueryJobListVo> pageInfo;
        PageHelper.startPage(pageNow, pageSize);
        try {
            pageInfo = streamJobService.getByProList(projectName, username, jobName, jobStatus, jobCreator, label, enable,jobType);
        } finally {
            PageHelper.clearPage();
        }

        return Message.ok().data("tasks", pageInfo.getList()).data("totalPage", pageInfo.getTotal());
    }

    @RequestMapping(path = "/jobInfo", method = RequestMethod.GET)
    public Message getJobList(HttpServletRequest req,
                              @RequestParam(value = "jobId", required = false) Integer jobId){
        String username = ModuleUserUtils.getOperationUser(req, "jobInfo");
        StreamJob streamJob = streamJobService.getJobById(jobId);
        return Message.ok().data("jobInfo",streamJob);
    }

    @RequestMapping(path = "/createOrUpdate", method = RequestMethod.POST)
    public Message createOrUpdate(HttpServletRequest req, @Validated @RequestBody MetaJsonInfo metaJsonInfo) {
        String username = ModuleUserUtils.getOperationUser(req, "create or update job");
        String projectName = metaJsonInfo.getProjectName();
        if (StringUtils.isBlank(projectName)){
            return Message.error("Project name cannot be empty(项目名不能为空，请指定)");
        }
        if(!this.privilegeService.hasEditPrivilege(req, projectName)){
            return Message.error("Have no permission to create or update StreamJob in project [" + projectName + "]");
        }
        StreamJobVersion job = streamJobService.createOrUpdate(username, metaJsonInfo);
        return Message.ok().data("jobId", job.getJobId());
    }

    @RequestMapping(path = "/updateLabel", method = RequestMethod.POST)
    public Message updateLabel(HttpServletRequest req, @RequestBody BulkUpdateLabelRequest bulkUpdateLabelRequest) {
        Message result = Message.ok("success");

        String userName = ModuleUserUtils.getOperationUser(req, "update Label");
        List<BulkUpdateLabel> tasksData = bulkUpdateLabelRequest.getTasks();
        List<StreamJob> jobList = new ArrayList<>();
        for (BulkUpdateLabel bulkUpdateLabel : tasksData) {
            Long jobId = bulkUpdateLabel.getId();
            StreamJob streamJob = this.streamJobService.getJobById(jobId);
            if (!streamJobService.isCreator(jobId, userName) &&
                    !this.privilegeService.hasEditPrivilege(req, streamJob.getProjectName())) {
                return Message.error("Have no permission to save StreamJob [" + jobId + "] configuration");
            }
            if (!streamJobService.getEnableStatus(jobId)){
                return Message.error("current Job " + streamJob.getName() + "has been banned, cannot updateLable,please enable job" );
            }
            String label = bulkUpdateLabel.getLabel();
            if (!RegularUtil.matches(label))
                return Message.error("Fail to save StreamJob label(保存/更新标签失败), message: " + "仅支持大小写字母、数字、下划线、小数点、逗号且长度小于64位  [" + jobId + "] ");
            StreamJob job = new StreamJob();
            job.setLabel(label);
            job.setId(jobId);
            jobList.add(job);
        }
        for (StreamJob streamJob : jobList) {
            streamJobService.updateLabel(streamJob);
        }
        return result;
    }

    @RequestMapping(path = "{jobId:\\w+}/versions", method = RequestMethod.GET)
    public Message versions(HttpServletRequest req, @PathVariable("jobId")Integer jobId,
                            @RequestParam(value = "pageNow", required = false) Integer pageNow,
                            @RequestParam(value = "pageSize", required = false) Integer pageSize){
        String userName = ModuleUserUtils.getOperationUser(req, "Query job version page");
        if (Objects.isNull(pageNow)) {
            pageNow = 1;
        }
        if (Objects.isNull(pageSize)){
            pageSize = 20;
        }
        StreamJob streamJob = this.streamJobService.getJobById(jobId);
        if (Objects.isNull(streamJob)){
            return Message.error("Unknown StreamJob with id: " + jobId + "(无法找到对应的流任务)");
        }
        if (!streamJobService.hasPermission(streamJob, userName)
                &&!this.privilegeService.hasAccessPrivilege(req, streamJob.getProjectName())){
                return Message.error("Have no permission to get Job details of StreamJob [" + jobId + "]");

        }
        Message result = Message.ok();
        PageHelper.startPage(pageNow, pageSize);
        try{
            PageInfo<VersionDetailVo> pageInfo = this.streamJobService.getVersionList(jobId);
            if (Objects.nonNull(pageInfo)){
                result.data("versions", pageInfo.getList());
                result.data("totalPage", pageInfo.getTotal());
            }
        } catch (Exception e){
            result = Message.error("Fail to query job version page (查看任务版本列表失败), message: " + e.getMessage());
        } finally{
            PageHelper.clearPage();
        }
        return result;
    }
    @RequestMapping(path = "/version", method = RequestMethod.GET)
    public Message version(HttpServletRequest req, @RequestParam(value = "jobId", required = false) Long jobId,
                           @RequestParam(value = "version", required = false) String version) throws JobException {
        if (jobId == null) {
            throw JobExceptionManager.createException(30301, "jobId");
        }
        if (StringUtils.isEmpty(version)) {
            throw JobExceptionManager.createException(30301, "version");
        }
        String username = ModuleUserUtils.getOperationUser(req, "view the job version");
        StreamJob streamJob = this.streamJobService.getJobById(jobId);
        if (!streamJobService.hasPermission(streamJob, username) &&
                !this.privilegeService.hasAccessPrivilege(req, streamJob.getProjectName())) {
            return Message.error("Have no permission to view versions of StreamJob [" + jobId + "]");
        }
        VersionDetailVo versionDetailVO = streamJobService.versionDetail(jobId, version);
        return Message.ok().data("detail", versionDetailVO);
    }

    @RequestMapping(path = "/ban", method = RequestMethod.POST)
    public Message banJob(HttpServletRequest req, @RequestBody List<Long> jobIdList) {
        String userName = ModuleUserUtils.getOperationUser(req, "ban job");
        Message result = Message.ok("success");

        HashMap<Long,StreamJob> jobMap = new HashMap<>();
        if (jobIdList.size() > 100){
            return Message.error("The number of the jobs has exceeded 100, please check!");
        }
        if (jobIdList.isEmpty()){
            return Message.error("there is no job to be banned, please check");
        }
        try {
            for (Long jobId : jobIdList) {
                LOG.info("{} try to ban job {}.",userName,jobId);
                StreamJob streamJob = this.streamJobService.getJobById(jobId);
                jobMap.put(jobId,streamJob);
                if (streamJob == null) {
                    return Message.error("not exists job " + jobId);
                }
                if (!streamJobService.isCreator(jobId, userName) &&
                        !this.privilegeService.hasEditPrivilege(req, streamJob.getProjectName())) {
                    return Message.error("Have no permission to ban StreamJob [" + jobId + "] configuration");
                }
                if (!streamJobService.canBeDisabled(jobId)){
                    return Message.error("current job [" + jobId + "] can not be banned, please check");
                }
            }
            for (Long jobId : jobIdList) {
                streamJobService.disableJob(jobMap.get(jobId));
            }
        } catch(Exception e) {
            String message = "Fail to ban StreamJob, message: " + e.getMessage();
            LOG.warn(message, e);
            result = Message.error(message);
        }
        return result;
    }

    @RequestMapping(path = "/enable", method = RequestMethod.POST)
    public Message enableJob(HttpServletRequest req, @RequestBody List<Long> jobIdList) {
        String userName = ModuleUserUtils.getOperationUser(req, "ban job");
        Message result = Message.ok("success");

        HashMap<Long,StreamJob> jobMap = new HashMap<>();
        if (jobIdList.size() > 100){
            return Message.error("The number of the jobs has exceeded 100, please check!");
        }
        if (jobIdList.isEmpty()){
            return Message.error("there is no job to be activated, please check");
        }

        try {
            for (Long jobId : jobIdList) {
                LOG.info("{} try to activate job {}.",userName,jobId);
                StreamJob streamJob = this.streamJobService.getJobById(jobId);
                jobMap.put(jobId,streamJob);
                if (streamJob == null) {
                    return Message.error("not exists job " + jobId);
                }
                if (!streamJobService.isCreator(jobId, userName) &&
                        !this.privilegeService.hasEditPrivilege(req, streamJob.getProjectName())) {
                    return Message.error("Have no permission to ban StreamJob [" + jobId + "] configuration");
                }
                if (!streamJobService.canbeActivated(jobId)){
                    return Message.error("current job [" + jobId + "] can not be activated, please check");
                }
            }
            for (Long jobId : jobIdList) {
                streamJobService.activateJob(jobMap.get(jobId));
            }
        } catch (Exception e) {
            String message = "Fail to activate StreamJob, message: " + e.getMessage();
            LOG.warn(message, e);
            result = Message.error(message);
        }
        return result;
    }
    /**
     * Inspect the execution
     * @param req request
     * @return message
     */
    @RequestMapping(path = "/execute/inspect", method = RequestMethod.PUT)
    public Message executeInspect(HttpServletRequest req, @RequestParam(value = "jobId")List<Integer> jobIdList){
        Message result = Message.ok();
        String userName = ModuleUserUtils.getOperationUser(req, "Inspect of execution");

        for (Integer jobId : jobIdList) {
            StreamJob streamJob = this.streamJobService.getJobById(jobId);
            if (Objects.isNull(streamJob)){
                return Message.error("Unknown StreamJob with id: " + jobId + "(无法找到对应的流任务)");
            }
            if (!streamJobService.hasPermission(streamJob, userName) &&
                    !this.privilegeService.hasEditPrivilege(req, streamJob.getProjectName())){
                return Message.error("Have no permission to inspect the StreamJob [" + jobId + "]");
            }

            String managementMode = Optional.ofNullable(this.streamJobConfService.getJobConfValue(jobId, JobConfKeyConstants.MANAGE_MODE_KEY().getValue()))
                    .orElse("defaultManagementMode");
            if (!Boolean.parseBoolean(JobLauncherConfiguration.ENABLE_FLINK_MANAGER_EC_ENABLE().getHotValue().toString()) &&
                    managementMode.equals("detach")){
                return Message.error("The system does not enable the detach feature ,detach job cannot start [" + jobId + "]");
            }
            if (!streamJobService.getEnableStatus(jobId)){
                return Message.error("current Job " + streamJob.getName() + "has been banned, cannot start,please enable job" );
            }
            try {
                HashMap<String, Object> jobConfig = new HashMap<>(this.streamJobConfService.getJobConfig(jobId));
                HashMap<String, Object> flinkProduce = (HashMap<String, Object>) jobConfig.get(JobConfKeyConstants.GROUP_PRODUCE().getValue());
                if (!flinkProduce.containsKey(JobConfKeyConstants.ALERT_USER().getValue())){
                    return Message.error("The StreamJob alarm recipient is not configured (未配置有效的失败告警用户)  [" + jobId + "]");
                } else {
                    String users = String.valueOf(flinkProduce.get(JobConfKeyConstants.ALERT_USER().getValue()));
                    if (users.isEmpty()){
                        return Message.error("The StreamJob alarm recipient is not configured (未配置有效的失败告警用户)  [" + jobId + "]");
                    }else {
                        List<String> userList=Arrays.asList(users.split(","));
                        int i =0;
                        for (String user :userList){
                            if (user.toLowerCase().contains("hduser")){
                                i++;
                            }
                        }
                        //防止配置多个hduser用户跳过验证。
                        if (userList.size()==i){
                            return Message.error("Please configure an alarm recipient other than hduser  [" + jobId + "]");
                        }
                    }
                }
            }catch(Exception e){
                String message = "Fail to view StreamJob configuration(查看任务配置失败), message: " + e.getMessage();
                LOG.warn(message, e);
                result = Message.error(message);
            }

            // Get inspect result of the job
            List<JobInspectVo> inspectResult = new ArrayList<>();
            List<String> inspections = new ArrayList<>();
            try {
                inspectResult = this.streamJobInspectService
                        .inspect(jobId, new JobInspectVo.Types[]{JobInspectVo.Types.VERSION, JobInspectVo.Types.SNAPSHOT, JobInspectVo.Types.LIST,JobInspectVo.Types.HIGHAVAILABLE});
                inspections = inspectResult.stream().map(JobInspectVo::getInspectName)
                        .collect(Collectors.toList());
            } catch (Exception e){
                LOG.warn(e.getMessage());
                return Message.error("Fail to inspect job " + jobId + " of the execution(任务执行前检查失败), message: " + e.getMessage());
            }

            HashMap<String, Object> inspectResultMap = new HashMap<>();
            inspectResult.forEach(inspect -> inspectResultMap.put(inspect.getInspectName(), inspect));
            if (!inspectResultMap.containsKey("snapshot")){
                String value = this.streamJobConfService.getJobConfValue(jobId, JobConfKeyConstants.START_AUTO_RESTORE_SWITCH().getValue());
                String msg;
                if (value.equals("ON")){
                    msg ="获取到了空的快照地址";
                }else {
                    msg ="任务未开启快照，无需检查快照地址";
                }
                inspections.add("snapshot");
                JobSnapshotInspectVo jobSnapshotInspectVo =new JobSnapshotInspectVo();
                jobSnapshotInspectVo.setPath(msg);
                inspectResultMap.put("snapshot",jobSnapshotInspectVo);
            }
            inspectResultMap.put("inspections", inspections);
            result.setData(inspectResultMap);
        }
        return result;
    }
    @RequestMapping(path = "/execute", method = RequestMethod.POST)
    public Message executeJob(HttpServletRequest req, @RequestBody Map<String, Object> json) throws JobException {
        String userName = ModuleUserUtils.getOperationUser(req, "execute job");
        if (!json.containsKey("jobId") || json.get("jobId") == null) {
            throw JobExceptionManager.createException(30301, "jobId");
        }
        long jobId = Long.parseLong(json.get("jobId").toString());
        LOG.info("{} try to execute job {}.", userName, jobId);
        StreamJob streamJob = this.streamJobService.getJobById(jobId);
        if(streamJob == null) {
            return Message.error("not exists job " + jobId);
        } else if(!JobConf.SUPPORTED_MANAGEMENT_JOB_TYPES().getValue().contains(streamJob.getJobType())) {
            return Message.error("Job " + streamJob.getName() + " is not supported to execute.");
        }
        if (!streamJobService.hasPermission(streamJob, userName) &&
                !this.privilegeService.hasEditPrivilege(req, streamJob.getProjectName())) {
            return Message.error("Have no permission to execute StreamJob [" + jobId + "]");
        }
        String managementMode = Optional.ofNullable(this.streamJobConfService.getJobConfValue(jobId, JobConfKeyConstants.MANAGE_MODE_KEY().getValue()))
                    .orElse("defaultManagementMode");
        if (!Boolean.parseBoolean(JobLauncherConfiguration.ENABLE_FLINK_MANAGER_EC_ENABLE().getHotValue().toString()) &&
                managementMode.equals("detach")){
            return Message.error("The system does not enable the detach feature ,detach job cannot start [" + jobId + "]");
        }
        JobHighAvailableVo inspectVo = highAvailableService.getJobHighAvailableVo(jobId);
        if (!inspectVo.isHighAvailable()){
            return Message.error("The master and backup cluster materials for" + "Job " + streamJob.getName() + "do not match, please check the material");
        }
        if (!streamJobService.getEnableStatus(jobId)){
            return Message.error("current Job " + streamJob.getName() + "has been banned, cannot start,please enable job" );
        }
        try {
            streamTaskService.execute(jobId, 0L, userName);
        } catch (Exception e) {
            LOG.error("{} execute job {} failed!", userName, jobId, e);
            return Message.error(ExceptionUtils.getRootCauseMessage(e));
        }
        return Message.ok();
    }

    @RequestMapping(path = "/stop", method = RequestMethod.GET)
    public Message killJob(HttpServletRequest req,
                           @RequestParam(value = "jobId", required = false) Long jobId,
                           @RequestParam(value = "snapshot", required = false) Boolean snapshot) throws JobException {
        String userName = ModuleUserUtils.getOperationUser(req, "stop the job");
        snapshot = !Objects.isNull(snapshot) && snapshot;
        if (jobId == null) {
            throw JobExceptionManager.createException(30301, "jobId");
        }
        LOG.info("{} try to kill job {}.", userName, jobId);
        StreamJob streamJob = this.streamJobService.getJobById(jobId);
        if(streamJob == null) {
            return Message.error("not exists job " + jobId);
        }
        if (!streamJobService.hasPermission(streamJob, userName) &&
                !this.privilegeService.hasEditPrivilege(req, streamJob.getProjectName())) {
            return Message.error("Have no permission to kill/stop StreamJob [" + jobId + "]");
        }
        if (!streamJobService.getEnableStatus(jobId)){
            return Message.error("current Job " + streamJob.getName() + "has been banned, cannot stop,please enable job" );
        }
        if(JobConf.SUPPORTED_MANAGEMENT_JOB_TYPES().getValue().contains(streamJob.getJobType())) {
            try {
                PauseResultVo resultVo = streamTaskService.pause(jobId, 0L, userName, snapshot);
                return snapshot? Message.ok().data("path", resultVo.getSnapshotPath()) : Message.ok();
            } catch (Exception e) {
                LOG.error("{} kill job {} failed!", userName, jobId, e);
                return Message.error(ExceptionUtils.getRootCauseMessage(e));
            }
        } else {
            LOG.error("{} try to kill not-supported-management job {} with name {}.", userName, jobId, streamJob.getName());
            return tryStopTask(streamJob, null);
        }
    }

    @RequestMapping(path = "/details", method = RequestMethod.GET)
    public Message detailsJob(HttpServletRequest req, @RequestParam(value = "jobId", required = false) Long jobId,
                              @RequestParam(value = "version", required = false) String version) throws JobException, JsonProcessingException {
        if (jobId == null) {
            JobExceptionManager.createException(30301, "jobId");
        }
        String username = ModuleUserUtils.getOperationUser(req, "view the job details");
        StreamJob streamJob = streamJobService.getJobById(jobId);
        if (!streamJobService.hasPermission(streamJob, username)
                && !this.privilegeService.hasAccessPrivilege(req, streamJob.getProjectName())){
                return Message.error("Have no permission to get Job details of StreamJob [" + jobId + "]");
        }
        if(streamJob == null) {
            return Message.error("not exists job " + jobId);
        }
        return Message.ok().data("details", streamTaskService.getJobDetailsVO(streamJob, version));
    }

    @RequestMapping(path = "/execute/history", method = RequestMethod.GET)
    public Message executeHistoryJob(HttpServletRequest req,
                                     @RequestParam(value = "jobId", required = false) Long jobId,
                                     @RequestParam(value = "version", required = false) String version) throws JobException {
        String username = ModuleUserUtils.getOperationUser(req, "view the job history");
        if (jobId == null) {
            throw JobExceptionManager.createException(30301, "jobId");
        }
        if (StringUtils.isEmpty(version)) {
            throw JobExceptionManager.createException(30301, "version");
        }
        StreamJob streamJob = this.streamJobService.getJobById(jobId);
        if (!streamJobService.hasPermission(streamJob, username) &&
                !this.privilegeService.hasAccessPrivilege(req, streamJob.getProjectName())){
            return Message.error("Have no permission to get Job details of StreamJob [" + jobId + "]");
        }
        List<StreamTaskListVo> details = streamTaskService.queryHistory(jobId, version);
        return Message.ok().data("details", details);
    }

    @RequestMapping(path = "/execute/errorMsg", method = RequestMethod.GET)
    public Message executeJobErrorMsg(HttpServletRequest req,
                                     @RequestParam(value = "jobId", required = false) Long jobId) throws JobException {
        String username = ModuleUserUtils.getOperationUser(req, "view the job history");
        if (jobId == null) {
            throw JobExceptionManager.createException(30301, "jobId");
        }
        StreamJob streamJob = this.streamJobService.getJobById(jobId);
        if (!streamJobService.hasPermission(streamJob, username) &&
                !this.privilegeService.hasAccessPrivilege(req, streamJob.getProjectName())){
            return Message.error("Have no permission to get Job details of StreamJob [" + jobId + "]");
        }
        StreamTask details = streamTaskService.queryErrorCode(jobId);
        return Message.ok().data("details", details);
    }

    private Message withStreamJob(HttpServletRequest req, String projectName,
                                  String jobName, String username,
                                  Function<StreamJob, Message> streamJobFunction) {
        if(StringUtils.isBlank(projectName)) {
            return Message.error("projectName cannot be empty!");
        } else if(StringUtils.isBlank(jobName)) {
            return Message.error("jobName cannot be empty!");
        }
        List<QueryJobListVo> streamJobs = streamJobService.getByProList(projectName, username, jobName, null, null,null, null,null).getList();
        if(CollectionUtils.isEmpty(streamJobs)) {
            return Message.error("Not exits Streamis job " + jobName);
        } else if(streamJobs.size() > 1) {
            return Message.error("Too many Streamis Job named " + jobName + ", we cannot distinguish between them.");
        } else if(!"spark.jar".equals(streamJobs.get(0).getJobType())) {
            return Message.error("Only spark.jar Job support to manage task.");
        }
        StreamJob streamJob = streamJobService.getJobById(streamJobs.get(0).getId());
        if (!streamJobService.hasPermission(streamJob, username) &&
                !this.privilegeService.hasEditPrivilege(req, streamJob.getProjectName())) {
            return Message.error("Have no permission to operate task for StreamJob [" + jobName + "].");
        }
        return streamJobFunction.apply(streamJob);
    }

    @RequestMapping(path = "/addTask", method = RequestMethod.GET)
    public Message addTask(HttpServletRequest req,
                           @RequestParam(value = "projectName") String projectName,
                           @RequestParam(value = "jobName") String jobName,
                           @RequestParam(value = "appId") String appId,
                           @RequestParam(value = "appUrl") String appUrl) {
        String username = ModuleUserUtils.getOperationUser(req, "add task");
        LOG.info("User {} try to add a new task for Streamis job {}.{} with appId: {}, appUrl: {}.", username, projectName, jobName, appId, appUrl);
        if(StringUtils.isBlank(appId)) {
            return Message.error("appId cannot be empty!");
        }
        return withStreamJob(req, projectName, jobName, username, streamJob -> {
            // 如果存在正在运行的，先将其停止掉
            StreamTask streamTask = streamTaskService.getLatestTaskByJobId(streamJob.getId());
            if(streamTask != null && JobConf.isRunning(streamTask.getStatus())) {
                LOG.warn("Streamis Job {} exists running task, update its status from Running to stopped at first.", jobName);
                streamTask.setStatus((Integer) JobConf.FLINK_JOB_STATUS_STOPPED().getValue());
                streamTask.setErrDesc("stopped by App's new task.");
                streamTaskService.updateTask(streamTask);
            }
            if(streamTask == null || StringUtils.isBlank(streamTask.getLinkisJobInfo())) {
                // 这里取个巧，从该工程该用户有权限的Job中找到一个Flink的历史作业，作为这个Spark Streaming作业的jobId和jobInfo
                // 替换掉JobInfo中的 yarn 信息，这样我们前端就可以在不修改任何逻辑的情况下正常展示Spark Streaming作业了
                PageInfo<QueryJobListVo> jobList = streamJobService.getByProList(streamJob.getProjectName(), username, null, null, null,null,null,null);
                List<QueryJobListVo> copyJobs = jobList.getList().stream().filter(job -> !job.getJobType().startsWith("spark."))
                        .collect(Collectors.toList());
                if(copyJobs.isEmpty()) {
                    return Message.error("no Flink Job has been submitted, the register to Streamis cannot be succeeded.");
                }
                int index = 0;
                streamTask = null;
                while(streamTask == null && index < copyJobs.size()) {
                    StreamTask copyTask = streamTaskService.getLatestTaskByJobId(copyJobs.get(index).getId());
                    if(copyTask == null || StringUtils.isBlank(copyTask.getLinkisJobInfo())) {
                        index ++;
                    } else {
                        LOG.warn("Streamis Job {} will bind the linkisJobInfo from history Flink Job {} with linkisJobId: {}, linkisJobInfo: {}.",
                                jobName, copyJobs.get(index).getName(), copyTask.getLinkisJobId(), copyTask.getLinkisJobInfo());
                        streamTask = streamTaskService.createTask(streamJob.getId(), (Integer) JobConf.FLINK_JOB_STATUS_RUNNING().getValue(), username);
                        streamTask.setLinkisJobId(copyTask.getLinkisJobId());
                        streamTask.setLinkisJobInfo(copyTask.getLinkisJobInfo());
                    }
                }
                if(streamTask == null) {
                    return Message.error("no Flink task has been executed, the register to Streamis cannot be succeeded.");
                }
            } else {
                StreamTask newStreamTask = streamTaskService.createTask(streamJob.getId(), (Integer) JobConf.FLINK_JOB_STATUS_RUNNING().getValue(), username);
                streamTask.setId(newStreamTask.getId());
                streamTask.setVersion(newStreamTask.getVersion());
                streamTask.setErrDesc("");
                streamTask.setStatus(newStreamTask.getStatus());
                streamTask.setSubmitUser(username);
            }
            streamTask.setStartTime(new Date());
            streamTask.setLastUpdateTime(new Date());
            StreamTask finalStreamTask = streamTask;
            return withFlinkJobInfo(jobName, streamTask.getLinkisJobInfo(), flinkJobInfo -> {
                flinkJobInfo.setApplicationId(appId);
                flinkJobInfo.setApplicationUrl(appUrl);
                flinkJobInfo.setName(jobName);
                flinkJobInfo.setStatus(JobConf.getStatusString(finalStreamTask.getStatus()));
                StreamTaskUtils.refreshInfo(finalStreamTask, flinkJobInfo);
                streamTaskService.updateTask(finalStreamTask);
                LOG.info("Streamis Job {} has added a new task successfully.", jobName);
                return Message.ok();
            });
        });
    }

    private Message withFlinkJobInfo(String jobName, String flinkJobInfoStr, Function<EngineConnJobInfo, Message> flinkJobInfoFunction) {
        EngineConnJobInfo flinkJobInfo;
        try {
            flinkJobInfo = DWSHttpClient.jacksonJson().readValue(flinkJobInfoStr, EngineConnJobInfo.class);
        } catch (JsonProcessingException e) {
            LOG.error("Job {} deserialize the flinkJobInfo string to object failed!", jobName, e);
            return Message.error("Deserialize the flinkJobInfo string to object failed!");
        }
        return flinkJobInfoFunction.apply(flinkJobInfo);
    }

    @RequestMapping(path = "/updateTask", method = RequestMethod.GET)
    public Message updateTask(HttpServletRequest req,
                              @RequestParam(value = "projectName") String projectName,
                              @RequestParam(value = "jobName") String jobName,
                              @RequestParam(value = "appId") String appId,
                              @RequestParam(value = "metrics") String metrics) {
        String username = ModuleUserUtils.getOperationUser(req, "update task");
        LOG.info("User {} try to update task for Streamis job {}.{} with appId: {}, metrics: {}.", username, projectName, jobName, appId, metrics);
        return withStreamJob(req, projectName, jobName, username, streamJob -> {
            StreamTask streamTask = streamTaskService.getLatestTaskByJobId(streamJob.getId());
            if (streamTask == null) {
                LOG.warn("Job {} is not exists running task, ignore to update its metrics.", jobName);
                return Message.ok("not exists running task, ignore it.");
            } else if (JobConf.isCompleted(streamTask.getStatus())) {
                LOG.warn("The task of job {} is completed, ignore to update its metrics.", jobName);
                return Message.ok("Task is completed, ignore to update its metrics.");
            }
            return withFlinkJobInfo(jobName, streamTask.getLinkisJobInfo(), flinkJobInfo -> {
                if (!flinkJobInfo.getApplicationId().equals(appId)) {
                    LOG.warn("Job {} with running task <appId: {}> is not equals to the request appId: {}, ignore to update its metrics.",
                            jobName, flinkJobInfo.getApplicationId(), appId);
                    return Message.ok("the request appId is not equals to the running task appId " + flinkJobInfo.getApplicationId());
                }
                JobStateInfo jobStateInfo = new JobStateInfo();
                jobStateInfo.setTimestamp(System.currentTimeMillis());
                jobStateInfo.setLocation(metrics);
                flinkJobInfo.setJobStates(new JobStateInfo[]{jobStateInfo});
                StreamTaskUtils.refreshInfo(streamTask, flinkJobInfo);
                streamTaskService.updateTask(streamTask);
                LOG.info("Streamis Job {} has updated the task metrics successfully.", jobName);
                return Message.ok();
            });
        });
    }

    @RequestMapping(path = "/updateTask", method = RequestMethod.POST)
    public Message updateTask(HttpServletRequest req,
                              @RequestBody Map<String, String> json) {
        String projectName = json.get("projectName");
        String jobName = json.get("jobName");
        String appId = json.get("appId");
        String metrics = json.get("metrics");
        return updateTask(req, projectName, jobName, appId, metrics);
    }

    @RequestMapping(path = "/stopTask", method = RequestMethod.GET)
    public Message stopTask(HttpServletRequest req,
                            @RequestParam(value = "projectName") String projectName,
                            @RequestParam(value = "jobName") String jobName,
                            @RequestParam(value = "appId") String appId,
                            @RequestParam(value = "appUrl") String appUrl) {
        String username = ModuleUserUtils.getOperationUser(req, "stop task");
        LOG.info("User {} try to stop task for Streamis job {}.{} with appId: {}, appUrl: {}.", username, projectName, jobName, appId, appUrl);
        return withStreamJob(req, projectName, jobName, username,
                streamJob -> tryStopTask(streamJob, appId));
    }

    private Message tryStopTask(StreamJob streamJob, String appId) {
        // 如果存在正在运行的，将其停止掉
        StreamTask streamTask = streamTaskService.getLatestTaskByJobId(streamJob.getId());
        if(streamTask != null && JobConf.isRunning(streamTask.getStatus())) {
            return withFlinkJobInfo(streamJob.getName(), streamTask.getLinkisJobInfo(), flinkJobInfo -> {
                if(appId == null || flinkJobInfo.getApplicationId().equals(appId)) {
                    LOG.warn("Streamis Job {} is exists running task, update its status to stopped.", streamJob.getName());
                    streamTask.setStatus((Integer) JobConf.FLINK_JOB_STATUS_STOPPED().getValue());
                    streamTask.setErrDesc("stopped by App itself.");
                    streamTaskService.updateTask(streamTask);
                    return Message.ok();
                } else {
                    LOG.warn("Job {} with running task <appId: {}> is not equals to the request appId: {}, ignore to stop it.",
                            streamJob.getName(), flinkJobInfo.getApplicationId(), appId);
                    return Message.ok("the request appId is not equals to the running task appId " + flinkJobInfo.getApplicationId());
                }
            });
        } else {
            LOG.warn("Streamis Job {} is not exists running task, ignore to stop it.", streamJob.getName());
            return Message.ok();
        }
    }

    @RequestMapping(path = "/progress", method = RequestMethod.GET)
    public Message progressJob(HttpServletRequest req, @RequestParam(value = "jobId", required = false) Long jobId,
                               @RequestParam(value = "version", required = false) String version) throws JobException {
        String username = ModuleUserUtils.getOperationUser(req, "view the job's progress");
        if (jobId == null) {
            throw JobExceptionManager.createException(30301, "jobId");
        }
        StreamJob streamJob = this.streamJobService.getJobById(jobId);
        if(streamJob == null) {
            return Message.error("not exists job " + jobId);
        } else if(!JobConf.SUPPORTED_MANAGEMENT_JOB_TYPES().getValue().contains(streamJob.getJobType())) {
            return Message.error("Job " + streamJob.getName() + " is not supported to get progress.");
        }
        if (!streamJobService.hasPermission(streamJob, username) &&
                !this.privilegeService.hasAccessPrivilege(req, streamJob.getProjectName())) {
            return Message.error("Have no permission to view the progress of StreamJob [" + jobId + "]");
        }
        JobProgressVo jobProgressVO = streamTaskService.getProgress(jobId, version);
        return Message.ok().data("taskId", jobProgressVO.getTaskId()).data("progress", jobProgressVO.getProgress());
    }

    @RequestMapping(path = "/jobContent", method = RequestMethod.GET)
    public Message uploadDetailsJob(HttpServletRequest req, @RequestParam(value = "jobId", required = false) Long jobId,
                                    @RequestParam(value = "version", required = false) String version) {
        String username = ModuleUserUtils.getOperationUser(req, "view job content");
        StreamJob streamJob = this.streamJobService.getJobById(jobId);
        if (!streamJobService.hasPermission(streamJob, username) &&
                !this.privilegeService.hasAccessPrivilege(req, streamJob.getProjectName())) {
            return Message.error("Have no permission to view job details of StreamJob [" + jobId + "]");
        }
        StreamisTransformJobContent jobContent = streamJobService.getJobContent(jobId, version);
        return Message.ok().data("jobContent", jobContent);
    }

    @RequestMapping(path = "/updateContent", method = RequestMethod.POST)
    public Message updateContent(HttpServletRequest req, @RequestBody UpdateContentRequest contentRequest) {
        String username = ModuleUserUtils.getOperationUser(req, "update job content");
        Long jobId = contentRequest.getJobId();
        String version = contentRequest.getVersion();
        StreamJob streamJob = this.streamJobService.getJobById(jobId);
        if (!streamJobService.hasPermission(streamJob, username) &&
                !this.privilegeService.hasEditPrivilege(req, streamJob.getProjectName())) {
            return Message.error("Have no permission to update job details of StreamJob [" + jobId + "]");
        }
        if (!streamJobService.getEnableStatus(jobId)){
            return Message.error("current Job " + streamJob.getName() + "has been banned, cannot update,please enable job" );
        }
        List<String> args = contentRequest.getArgs();
        if (args == null){
            boolean isHighAvailable = contentRequest.isHighAvailable();
            String highAvailableMessage = contentRequest.getHighAvailableMessage();
            StreamisTransformJobContent jobContent = streamJobService.updateArgs(jobId, version,null,isHighAvailable,highAvailableMessage);
            return Message.ok().data("jobContent", jobContent);
        } else {
            int hotValue =  Integer.parseInt(JobConf.DEFAULT_ARGS_LENGTH().getHotValue().toString());
            if (args.toString().length() > hotValue ){
                return Message.error("args length is too long, please less than "+ hotValue);
            }
            StreamisTransformJobContent jobContent = streamJobService.updateArgs(jobId, version,args,false,null);
            return Message.ok().data("jobContent", jobContent);
        }
    }

    @RequestMapping(path = "/alert", method = RequestMethod.GET)
    public Message getAlert(HttpServletRequest req, @RequestParam(value = "jobId", required = false) Long jobId,
                            @RequestParam(value = "version", required = false) String version) {
        String username = ModuleUserUtils.getOperationUser(req, "get alert message list");
        return Message.ok().data("list", streamJobService.getAlert(username, jobId, version));
    }

    @RequestMapping(path = "/logs", method = RequestMethod.GET)
    public Message getLog(HttpServletRequest req,
                          @RequestParam(value = "jobId", required = false) Long jobId,
                          @RequestParam(value = "taskId", required = false) Long taskId,
                          @RequestParam(value = "pageSize", defaultValue = "100") Integer pageSize,
                          @RequestParam(value = "fromLine", defaultValue = "1") Integer fromLine,
                          @RequestParam(value = "ignoreKeywords", required = false) String ignoreKeywords,
                          @RequestParam(value = "onlyKeywords", required = false) String onlyKeywords,
                          @RequestParam(value = "logType", required = false) String logType,
                          @RequestParam(value = "lastRows", defaultValue = "0") Integer lastRows) throws JobException {
        if (jobId == null) {
            throw JobExceptionManager.createException(30301, "jobId");
        }
        logType = StringUtils.isBlank(logType) ? "client" : logType;
        String username = ModuleUserUtils.getOperationUser(req, "view job logs");
        StreamJob streamJob = this.streamJobService.getJobById(jobId);
        if(streamJob == null) {
            return Message.error("not exists job " + jobId);
        } else if(!JobConf.SUPPORTED_MANAGEMENT_JOB_TYPES().getValue().contains(streamJob.getJobType()) &&
                "client".equals(logType)) {
            return Message.error("Job " + streamJob.getName() + " is not supported to get client logs.");
        }
        if (!streamJobService.hasPermission(streamJob, username) &&
                !this.privilegeService.hasAccessPrivilege(req, streamJob.getProjectName())){
            return Message.error("Have no permission to get Job details of StreamJob [" + jobId + "]");
        }
        LogRequestPayload payload = new LogRequestPayload();
        payload.setFromLine(fromLine);
        payload.setIgnoreKeywords(ignoreKeywords);
        payload.setLastRows(lastRows);
        payload.setOnlyKeywords(onlyKeywords);
        payload.setLogType(logType);
        payload.setPageSize(pageSize);
        RealtimeLogEntity realtimeLog = streamTaskService.getRealtimeLog(jobId, null != taskId ? taskId : 0L, username, payload);
        return Message.ok().data("logs",realtimeLog);
    }

    /**
     * Refresh the job status
     * @return status list
     */
    @RequestMapping(path = "/status", method = RequestMethod.PUT)
    public Message status(@RequestBody Map<String, List<Long>> requestMap){
        List<Long> jobIds = requestMap.get("id_list");
        if (Objects.isNull(jobIds) || jobIds.isEmpty()){
            return Message.error("The list of job id which to refresh the status cannot be null or empty");
        }
        Message result = Message.ok("success");
        try{
            result.data("result", this.streamTaskService.getStatusList(new ArrayList<>(jobIds)));
        }catch (Exception e){
            String message = "Fail to refresh the status of jobs(刷新/获得任务状态失败), message: " + e.getMessage();
            LOG.warn(message, e);
            result = Message.error(message, e);
        }
        return result;
    }

    /**
     * Do snapshot
     * @return path message
     */
    @RequestMapping(path = "/snapshot/{jobId:\\w+}", method = RequestMethod.PUT)
    public Message snapshot(@PathVariable("jobId")Long jobId, HttpServletRequest request){
        Message result = Message.ok();
        try{
            String username = ModuleUserUtils.getOperationUser(request, "do snapshot of job");
            StreamJob streamJob = this.streamJobService.getJobById(jobId);
            if(streamJob == null) {
                return Message.error("not exists job " + jobId);
            } else if(!JobConf.SUPPORTED_MANAGEMENT_JOB_TYPES().getValue().contains(streamJob.getJobType())) {
                return Message.error("Job " + streamJob.getName() + " is not supported to do snapshot.");
            }
            if (!streamJobService.hasPermission(streamJob, username) &&
                    !this.privilegeService.hasEditPrivilege(request, streamJob.getProjectName())){
                return Message.error("Have no permission to do snapshot for StreamJob [" + jobId + "]");
            }
            result.data("path", streamTaskService.snapshot(jobId, 0L, username));
        }catch (Exception e){
            String message = "Fail to do a snapshot operation (快照生成失败), message: " + e.getMessage();
            LOG.warn(message, e);
            result = Message.error(message, e);
        }
        return result;
    }

}
