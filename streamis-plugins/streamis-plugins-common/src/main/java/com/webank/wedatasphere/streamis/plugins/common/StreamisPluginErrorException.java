package com.webank.wedatasphere.streamis.plugins.common;

import com.webank.wedatasphere.linkis.common.exception.ErrorException;

/**
 * created by yangzhiyue on 2021/4/27
 * Description:
 */
public class StreamisPluginErrorException extends ErrorException {


    public StreamisPluginErrorException(int errCode, String desc) {
        super(errCode, desc);
    }

    public StreamisPluginErrorException(int errCode, String desc, Throwable throwable) {
        super(errCode, desc);
        this.initCause(throwable);
    }


}
