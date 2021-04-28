package com.webank.wedatasphere.streamis.datasource.appconn.exception;

import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;

/**
 * created by yangzhiyue on 2021/4/26
 * Description:
 */
public class StreamisDatasourceExternalException extends ExternalOperationFailedException {

    public StreamisDatasourceExternalException(int errorCode, String message) {
        super(errorCode, message);
    }

    public StreamisDatasourceExternalException(int errorCode, String message, Throwable cause) {
        super(errorCode, message);
        this.initCause(cause);
    }
}
