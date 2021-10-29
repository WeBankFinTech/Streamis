package com.webank.wedatasphere.streamis.appconn.exception;

import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

/**
 * created by yangzhiyue on 2021/4/25
 * Description:
 */
public class StreamisExternalException extends ExternalOperationFailedException {



    public StreamisExternalException(int errorCode, String message) {
        super(errorCode, message);
    }

    public StreamisExternalException(int errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
        this.initCause(cause);
    }
}
