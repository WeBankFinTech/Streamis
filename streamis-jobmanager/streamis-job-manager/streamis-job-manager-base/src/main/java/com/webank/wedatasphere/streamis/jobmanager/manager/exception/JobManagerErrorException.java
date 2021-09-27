package com.webank.wedatasphere.streamis.jobmanager.manager.exception;

import com.webank.wedatasphere.linkis.common.exception.ErrorException;

public class JobManagerErrorException extends ErrorException{
    public JobManagerErrorException(int errCode, String desc) {
        super(errCode, desc);
    }

    public JobManagerErrorException(int errCode, String desc, String ip, int port, String serviceKind) {
        super(errCode, desc, ip, port, serviceKind);
    }
}
