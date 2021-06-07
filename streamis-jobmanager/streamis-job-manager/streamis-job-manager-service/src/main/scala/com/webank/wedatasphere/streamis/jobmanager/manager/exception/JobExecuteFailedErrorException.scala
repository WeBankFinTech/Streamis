package com.webank.wedatasphere.streamis.jobmanager.manager.exception

import com.webank.wedatasphere.linkis.common.exception.ErrorException

/**
 *
 * @date 2021-06-06
 * @author enjoyyin
 * @since 0.5.0
 */
class JobExecuteFailedErrorException(errorCode: Int, errorMsg: String) extends ErrorException(errorCode, errorMsg)

class JobStopFailedErrorException(errorCode: Int, errorMsg: String) extends ErrorException(errorCode, errorMsg)

class JobFetchFailedErrorException(errorCode: Int, errorMsg: String) extends ErrorException(errorCode, errorMsg)