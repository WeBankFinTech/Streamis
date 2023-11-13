package com.webank.wedatasphere.streamis.jobmanager.service;

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.JobHighAvailableVo;

public interface HighAvailableService {

    JobHighAvailableVo getJobHighAvailableVo(long jobId);
}
