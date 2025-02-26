package com.webank.wedatasphere.streamis.jobmanager.manager.exception;

import org.apache.linkis.common.exception.WarnException;

public class JsonFormatWarnException extends WarnException {

    public JsonFormatWarnException(int errCode, String desc) {
        super(errCode, desc);
    }

    public JsonFormatWarnException(int errCode, String desc, Throwable t) {
        super(errCode, desc);
        initCause(t);
    }
}
