package com.webank.wedatasphere.streamis.jobmanager.service;

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.JobHighAvailableVo;
import com.webank.wedatasphere.streamis.jobmanager.vo.HighAvailableMsg;

public interface HighAvailableService {

    JobHighAvailableVo getJobHighAvailableVo(long jobId);

    HighAvailableMsg getHighAvailableMsg();

    Boolean canBeStarted(Long jobId);

    Boolean confirmToken(String token);
}
