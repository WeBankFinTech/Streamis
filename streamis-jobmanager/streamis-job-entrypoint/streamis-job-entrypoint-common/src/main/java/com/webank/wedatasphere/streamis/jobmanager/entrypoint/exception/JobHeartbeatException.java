package com.webank.wedatasphere.streamis.jobmanager.entrypoint.exception;

import org.apache.linkis.common.exception.ErrorException;

public class JobHeartbeatException extends ErrorException {

    public JobHeartbeatException(int errCode, String desc) {
        super(errCode, desc);
    }

}
