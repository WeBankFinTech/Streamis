package com.webank.wedatasphere.streamis.jobmanager.manager.exception;


import org.apache.linkis.common.exception.ErrorException;

public class HookInitException extends ErrorException {


    public HookInitException(String desc) {
        super(JobManagerErrorCode.HOOK_INIT_ERROR_CODE, desc);
    }

}
