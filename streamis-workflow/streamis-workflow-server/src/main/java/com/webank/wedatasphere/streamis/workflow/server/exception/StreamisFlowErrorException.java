package com.webank.wedatasphere.streamis.workflow.server.exception;

import com.webank.wedatasphere.linkis.common.exception.ErrorException;

/**
 * created by yangzhiyue on 2021/4/19
 * Description:
 */
public class StreamisFlowErrorException extends ErrorException {





    public StreamisFlowErrorException(int errCode, String desc) {
        super(errCode, desc);
    }

    public StreamisFlowErrorException(int errCode, String desc, Throwable t){
        super(errCode, desc);
        this.initCause(t);
    }


}
