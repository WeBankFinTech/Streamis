package com.webank.wedatasphere.streamis.jobmanager.manager.service;

import org.apache.linkis.common.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class InstanceService {

    private static final Logger logger = LoggerFactory.getLogger(InstanceService.class);
    @Value("${server.port}")
    private String serverPort;

    public String getThisServiceInstance() {

        String hostname = Utils.getComputerName();
        int port = 0;
        try {
            port = Integer.parseInt(serverPort);
        } catch (Exception e){
            logger.warn("Parse system property 'server.port' failed. {}", e.getMessage());
        }
        return hostname + ':' + port;
    }
}
