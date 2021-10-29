package com.webank.wedatasphere.streamis.jobmanager.manager.exception

import com.webank.wedatasphere.linkis.common.exception.ErrorException

class JobExecuteFailedErrorException(errorCode: Int, errorMsg: String) extends ErrorException(errorCode, errorMsg)

class JobStopFailedErrorException(errorCode: Int, errorMsg: String) extends ErrorException(errorCode, errorMsg)

class JobFetchFailedErrorException(errorCode: Int, errorMsg: String) extends ErrorException(errorCode, errorMsg)
