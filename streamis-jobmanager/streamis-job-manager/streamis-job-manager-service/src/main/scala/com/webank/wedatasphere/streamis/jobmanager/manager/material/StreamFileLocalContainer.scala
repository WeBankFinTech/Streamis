package com.webank.wedatasphere.streamis.jobmanager.manager.material

import org.apache.linkis.common.conf.CommonVars

trait StreamFileLocalContainer extends StreamFileContainer {

}

object StreamFileLocalContainer{

  val STORE_PATH: CommonVars[String] = CommonVars("wds.streamis.job.material.container.local.store-path", "material")
}