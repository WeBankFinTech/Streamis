package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception

import com.webank.wedatasphere.linkis.common.exception.ErrorException

/**
 *
 * @date 2021-06-06
 * @author enjoyyin
 * @since 0.5.0
 */
class FlinkJobLaunchErrorException(errorCode: Int, errorMsg: String) extends ErrorException(errorCode, errorMsg) {

}
