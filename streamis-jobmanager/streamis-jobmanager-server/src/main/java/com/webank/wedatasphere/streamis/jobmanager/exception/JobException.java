package com.webank.wedatasphere.streamis.jobmanager.exception;

import com.webank.wedatasphere.linkis.common.exception.ErrorException;

public class JobException extends ErrorException {

    public JobException(int errCode, String desc) {
        super(errCode, desc);
    }

    public JobException(int errCode, String desc, String ip, int port, String serviceKind) {
        super(errCode, desc, ip, port, serviceKind);
    }
}
