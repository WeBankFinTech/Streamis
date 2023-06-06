package com.webank.wedatasphere.streamis.jobmanager.manager.entity;

import com.webank.wedatasphere.streamis.jobmanager.launcher.enums.JobClientType;


/**
 * Stream job mode
 */
public enum StreamJobMode {
    /**
     * Engine Conn mode
     */
    engineConn(JobClientType.ATTACH.getName()),

    /**
     * Client mode
     */
    client(JobClientType.DETACH.getName());

    /**
     * According to client type
     */
    private final String clientType;

    StreamJobMode(String clientType){
        this.clientType = clientType;
    }

    public String getClientType(){
        return this.clientType;
    }
}
