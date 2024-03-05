package com.webank.wedatasphere.streamis.jobmanager.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamisFile;
import com.webank.wedatasphere.streamis.jobmanager.manager.service.BMLService;
import com.webank.wedatasphere.streamis.jobmanager.service.JobManagerService;
import org.apache.linkis.common.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.InputStream;
import java.util.Map;

public class JobManagerServiceImpl implements JobManagerService {

    @Autowired
    private BMLService bmlService;

    @Override
    public InputStream download(StreamisFile files) throws JsonProcessingException {
        Map<String,String> map = JsonUtils.jackson().readValue(files.getStorePath(), Map.class);
        return bmlService.get(files.getCreateBy(), map.get("resourceId"), map.get("version"));
    }
}
