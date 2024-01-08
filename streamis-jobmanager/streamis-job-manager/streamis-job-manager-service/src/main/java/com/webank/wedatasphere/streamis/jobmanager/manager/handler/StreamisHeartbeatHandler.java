package com.webank.wedatasphere.streamis.jobmanager.manager.handler;

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.JobConf;
import com.webank.wedatasphere.streamis.jobmanager.manager.alert.AlertLevel;
import com.webank.wedatasphere.streamis.jobmanager.manager.alert.Alerter;
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.StreamJobMapper;
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.StreamRegisterMapper;
import com.webank.wedatasphere.streamis.jobmanager.manager.dao.StreamTaskMapper;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJob;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamRegister;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamTask;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.StreamJobService;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Component
public class StreamisHeartbeatHandler {

    @Autowired
    private StreamRegisterMapper streamRegisterMapper;

    @Autowired
    private StreamJobMapper streamJobMapper;

    @Autowired
    private StreamTaskMapper streamTaskMapper;

    @Autowired
    private StreamJobService jobService;

    @Autowired
    private Alerter[] alert;

    private static final Logger logger = LoggerFactory.getLogger(StreamisHeartbeatHandler.class);

    @PostConstruct
    public void startHeartbeatCheckThread() {
        if ((Boolean) JobConf.LOGS_HEARTBEAT_CHECK_ENABLE().getValue()) {
            int interval = Integer.parseInt(JobConf.LOGS_HEARTBEAT_CHECK_INTERVAL().getHotValue().toString());
            Utils.defaultScheduler().scheduleAtFixedRate(() -> {
                try {
                    checkRegisterStatus();
                    checkHeartbeatStatus();
                } catch (Exception e) {
                    logger.error("stream checkHeartbeatStatus failed");
                }
            }, 1L *60 *1000, interval ,TimeUnit.MILLISECONDS);
            logger.info("heart beat info check started.");
        }

    }

    public void checkHeartbeatStatus() {
        List<StreamRegister> streamRegisterInfo = streamRegisterMapper.getInfo();
        if (!streamRegisterInfo.isEmpty()) {
            for (StreamRegister streamRegister : streamRegisterInfo) {
                StreamTask lastTask = streamTaskMapper.getLatestByJobId(streamRegister.getJobId());
                if (!JobConf.isRunning(lastTask.getStatus())) {
                    continue;
                }
                Date heartbeatTime = streamRegister.getHeartbeatTime();
                String applicationName = streamRegister.getApplicationName();
                String[] parts = applicationName.split("\\.");
                String projectName = parts[0];
                String jobName = parts[1];
                long currentTime = System.currentTimeMillis();
                long diffInMillies = currentTime - heartbeatTime.getTime();
                int timeout = Integer.parseInt(JobConf.LOGS_HEARTBEAT_INTERVAL_TIMEOUT().getHotValue().toString());
                if (diffInMillies > timeout) {
                    StreamJob job = streamJobMapper.getCurrentJob(projectName, jobName);
                    String alertMsg = "流式" + job.getJobType() + "应用[" + job.getProjectName() + "." + job.getName() + "] 回调日志心跳超过" + timeout + "ms未响应, 请及时确认应用是否正常！";
                    logger.warn(alertMsg);
                    if (Boolean.parseBoolean(JobConf.LOGS_HEARTBEAT_ALARMS_ENABLE().getHotValue().toString())) {
                        List<String> userList = getAllAlertUsers(job);
                        StreamTask streamTask = streamTaskMapper.getLatestByJobId(job.getId());
                        alert(jobService.getAlertLevel(job), alertMsg, userList, streamTask);
                    }
                }
            }
        }
    }

    private void checkRegisterStatus() {
        ArrayList<Integer> statusList = new ArrayList<>();
        statusList.add((int)JobConf.FLINK_JOB_STATUS_RUNNING().getValue());
        List<StreamTask> streamTasks = streamTaskMapper.getTasksByStatus(statusList);
        if (null == streamTasks || streamTasks.isEmpty()) {
            return ;
        }
        for (StreamTask streamTask : streamTasks) {
            StreamJob streamJob = streamJobMapper.getJobById(streamTask.getJobId());
            String appName = streamJob.getProjectName() +"."+streamJob.getName();
            StreamRegister register = streamRegisterMapper.getInfoByApplicationName(appName);
            if (null == register) {
                List<String> userList = getAllAlertUsers(streamJob);
                String alertMsg ="流式" + streamJob.getJobType() + "应用[" + appName + "] 回调日志没有注册, 请及时确认应用是否正常！";
                logger.info(alertMsg);
                //todo 自定义告警级别
                if ((boolean) JobConf.LOGS_HEARTBEAT_REGISTER_ALARMS_ENABLE().getHotValue()) {
                    alert(jobService.getAlertLevel(streamJob), alertMsg, userList, streamTask);
                }
            }
        }
    }

    protected void alert(AlertLevel alertLevel, String alertMsg, List<String> users, StreamTask streamTask) {
        for (Alerter alerter : alert) {
            try {
                alerter.alert(alertLevel, alertMsg, users, streamTask);
            } catch (Exception t) {
                logger.error("failed to send alert message to " + alerter.getClass().getSimpleName() + ".", t);
            }
        }
    }

    protected List<String> getAllAlertUsers(StreamJob job) {
        Set<String> allUsers = new LinkedHashSet<>();
        List<String> alertUsers = jobService.getAlertUsers(job);
        boolean isValid = false;
        if (alertUsers != null) {
            for (String user : alertUsers) {
                if (StringUtils.isNotBlank(user) && !user.toLowerCase().contains("hduser")) {
                    isValid = true;
                    allUsers.add(user);
                }
            }
            if (!allUsers.contains(job.getSubmitUser())) {
                allUsers.add(job.getSubmitUser());
            }
        }
        if (!isValid) {
            allUsers.add(job.getSubmitUser());
            allUsers.add(job.getCreateBy());
        }
        return new ArrayList<>(allUsers);
    }

}
