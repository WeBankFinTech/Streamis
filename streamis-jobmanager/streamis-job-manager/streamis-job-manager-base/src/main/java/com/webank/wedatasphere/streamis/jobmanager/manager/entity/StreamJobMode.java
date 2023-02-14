package com.webank.wedatasphere.streamis.jobmanager.manager.entity;

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.type.JobClientType;
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.type.JobClientType$;
import scala.Enumeration;

/**
 * Stream job mode
 */
public enum StreamJobMode {
    /**
     * Engine Conn mode
     */
    EngineConn(JobClientType.ATTACH()),

    /**
     * Client mode
     */
    Client(JobClientType.DETACH());

    /**
     * According to client type
     */
    private final Enumeration.Value clientType;

    StreamJobMode(JobClientType$.Value clientType){
        this.clientType = clientType;
    }

    public Enumeration.Value getClientType(){
        return this.clientType;
    }
}
