package com.webank.wedatasphere.streamis.jobmanager.service.impl;

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.conf.JobConf;
import com.webank.wedatasphere.streamis.jobmanager.launcher.service.StreamJobConfService;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJobVersion;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.JobHighAvailableVo;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.DefaultStreamJobService;
import com.webank.wedatasphere.streamis.jobmanager.manager.utils.SourceUtils;
import com.webank.wedatasphere.streamis.jobmanager.restful.api.JobRestfulApi;
import com.webank.wedatasphere.streamis.jobmanager.service.HighAvailableService;
import com.webank.wedatasphere.streamis.jobmanager.utils.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        String highAvailablePolicy = this.streamJobConfService.getJobConfValue(jobId, JobConf.HIGHAVAILABLE_POLICY().key());
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
}
