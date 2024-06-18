package com.webank.wedatasphere.streamis.jobmanager.service.impl;

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.JobConf;
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.StreamJobLauncherConf;
import com.webank.wedatasphere.streamis.jobmanager.launcher.service.StreamJobConfService;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJobVersion;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.JobHighAvailableVo;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.DefaultStreamJobService;
import com.webank.wedatasphere.streamis.jobmanager.manager.utils.SourceUtils;
import com.webank.wedatasphere.streamis.jobmanager.restful.api.JobRestfulApi;
import com.webank.wedatasphere.streamis.jobmanager.service.HighAvailableService;
import com.webank.wedatasphere.streamis.jobmanager.utils.JsonUtil;
import com.webank.wedatasphere.streamis.jobmanager.vo.HighAvailableMsg;
import org.apache.linkis.common.utils.Utils;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.executable.ValidateOnExecution;
import java.net.InetAddress;
import java.util.Map;
import java.util.Optional;

@Service
public class HighAvailableServiceImpl implements HighAvailableService {

    private static final Logger LOG = LoggerFactory.getLogger(JobRestfulApi.class);
    @Autowired
    private DefaultStreamJobService defaultStreamJobService;

    @Autowired
    private StreamJobConfService streamJobConfService;

    @Override
    public JobHighAvailableVo getJobHighAvailableVo(long jobId){
        StreamJobVersion jobVersion = this.defaultStreamJobService.getLatestJobVersion(jobId);
        String highAvailablePolicy = this.streamJobConfService.getJobConfValue(jobId, JobConf.HIGHAVAILABLE_POLICY_KEY().getValue());
        JobHighAvailableVo inspectVo = new JobHighAvailableVo();
        Optional<String> sourceOption = Optional.ofNullable(jobVersion.getSource());
        if(sourceOption.isPresent() && JsonUtil.isJson(sourceOption.get())) {
            String source = sourceOption.get();
            inspectVo = SourceUtils.manageJobProjectFile(highAvailablePolicy, source);
        } else {
            LOG.warn("this job source is null");
            inspectVo.setHighAvailable(true);
            inspectVo.setMsg("User changed params of job not by deploy, will skip to check its highavailable(用户未走发布单独修改了job信息，跳过高可用检查)");
        }
        return inspectVo;
    }

    @Override
    public HighAvailableMsg getHighAvailableMsg(){
        HighAvailableMsg msg = new HighAvailableMsg();
        msg.setClusterName(StreamJobLauncherConf.HIGHAVAILABLE_CLUSTER_NAME().getHotValue());
        msg.setWhetherManager(Boolean.parseBoolean(StreamJobLauncherConf.WHETHER_MANAGER_CLUSTER().getHotValue().toString()));
        msg.setNodeIp(Utils.getComputerName());
        return msg;
    }

    @Override
    public Boolean canBeStarted(Long jobId){
        String highAvailablePolicy = this.streamJobConfService.getJobConfValue(jobId, JobConf.HIGHAVAILABLE_POLICY_KEY().getValue());
        highAvailablePolicy = Optional.ofNullable(highAvailablePolicy).orElse(JobConf.HIGHAVAILABLE_DEFAULT_POLICY().getHotValue());
        HighAvailableMsg msg = this.getHighAvailableMsg();
        if(highAvailablePolicy.equals(JobConf.HIGHAVAILABLE_POLICY_MANAGERSLAVE().getValue()) || highAvailablePolicy.equals(JobConf.HIGHAVAILABLE_POLICY_MANAGERSLAVE_BAK().getValue())){
            return msg.getWhetherManager();
        }
        return true;
    }

    @Override
    public Boolean confirmToken(String source){
        Optional<String> sourceOption = Optional.ofNullable(source);
        if(sourceOption.isPresent() && JsonUtil.isJson(sourceOption.get())) {
            String sourceStr = sourceOption.get();
            Map sourceMap = BDPJettyServerHelper.gson().fromJson(sourceStr, Map.class);
            if (sourceMap.containsKey("source")) {
                String sourceValue = sourceMap.get("source").toString();
                if (sourceValue.equals(JobConf.HIGHAVAILABLE_SOURCE().getValue())) {
                    if (sourceMap.containsKey("token")) {
                        String tokenContent = sourceMap.get("token").toString();
                        return tokenContent.equals(JobConf.HIGHAVAILABLE_TOKEN().getValue());
                    }
                }
            }
        }
        return false;
    }
}
