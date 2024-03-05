package com.webank.wedatasphere.streamis.jobmanager.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamisFile;

import java.io.InputStream;

public interface JobManagerService {

    InputStream download(StreamisFile file) throws JsonProcessingException;
}
