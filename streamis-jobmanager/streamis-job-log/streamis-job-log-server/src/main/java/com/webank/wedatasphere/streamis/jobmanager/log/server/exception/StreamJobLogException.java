package com.webank.wedatasphere.streamis.jobmanager.log.server.exception;

import org.apache.linkis.common.exception.ErrorException;
import org.apache.linkis.common.exception.ExceptionLevel;
import org.apache.linkis.common.exception.LinkisRuntimeException;

/**
 * Stream job log exception
 */
public class StreamJobLogException extends ErrorException {
    public StreamJobLogException(int errCode, String desc) {
        super(errCode, desc);
    }
    public StreamJobLogException(int errCode, String desc, Throwable t){
        super(errCode, desc);

    }
    public static class Runtime extends LinkisRuntimeException{

        public Runtime(int errCode, String desc) {
            super(errCode, desc);
        }

        @Override
        public ExceptionLevel getLevel() {
            return ExceptionLevel.ERROR;
        }
    }
}
