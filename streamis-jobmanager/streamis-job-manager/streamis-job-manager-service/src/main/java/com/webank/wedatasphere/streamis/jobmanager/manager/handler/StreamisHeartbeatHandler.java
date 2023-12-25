package com.webank.wedatasphere.streamis.jobmanager.manager.handler;

import com.webank.wedatasphere.streamis.errorcode.manager.StreamisErrorCodeManager;
import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.User;
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.JobConf;
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
    private Alerter alert;

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisHeartbeatHandler.class);

    @PostConstruct
    public void startHeartbeatCheckThread() {
        int interval = Integer.parseInt(JobConf.LOGS_HEARTBEAT_CHECK_INTERVAL().getHotValue().toString());
        Utils.defaultScheduler().scheduleAtFixedRate(() -> {
            try {
                checkHeartbeatStatus();
            } catch (Exception e) {
                LOGGER.error("stream checkHeartbeatStatus failed");
            }
        }, 1L *60 *1000, interval ,TimeUnit.MILLISECONDS);
    }

    public void checkHeartbeatStatus() {
        List<StreamRegister> streamRegisterInfo = streamRegisterMapper.getInfo();
        if (!streamRegisterInfo.isEmpty()) {
            for (StreamRegister streamRegister : streamRegisterInfo) {
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
                    String alertMsg = "Flink应用[" + job.getName() + "] 回调日志心跳超时, 请及时确认应用是否正常！";
                    LOGGER.info(alertMsg);
                    if (!Boolean.parseBoolean(JobConf.LOGS_HEARTBEAT_ALARMS_ENABLE().getHotValue().toString())) {
                        List<String> userList = getAlertUsers(job);
                        StreamTask streamTask = streamTaskMapper.getLatestByJobId(job.getId());
                        alert.alert(jobService.getAlertLevel(job), alertMsg, userList, streamTask);
                    }
                }
            }
        }
    }


    protected List<String> getAlertUsers(StreamJob job) {
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
