package com.webank.wedatasphere.streamis.jobmanager.launcher.service

import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.client.LinkisFlinkManagerClient
import org.springframework.stereotype.Service

import javax.annotation.PostConstruct

@Service
class LinkisFlinkManagerECRefreshService {

  @PostConstruct
  def init(): Unit = {
    LinkisFlinkManagerClient.initScheduledTask()
  }

}
