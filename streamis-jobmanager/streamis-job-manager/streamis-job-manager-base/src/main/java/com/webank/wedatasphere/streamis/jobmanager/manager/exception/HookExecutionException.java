package com.webank.wedatasphere.streamis.jobmanager.manager.exception;

import org.apache.linkis.common.exception.ErrorException;

public class HookExecutionException extends ErrorException {


    public HookExecutionException(String desc) {
        super(JobManagerErrorCode.HOOK_EXECUTION_ERROR_CODE, desc);
    }
}
