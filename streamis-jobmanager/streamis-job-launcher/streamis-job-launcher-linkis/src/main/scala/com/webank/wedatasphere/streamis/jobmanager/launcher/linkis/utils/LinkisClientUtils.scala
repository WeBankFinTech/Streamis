package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.utils

import org.apache.linkis.computation.client.once.{LinkisManagerClient, LinkisManagerClientImpl}
import org.apache.linkis.computation.client.once.simple.SimpleOnceJobBuilder
import org.apache.linkis.httpclient.dws.DWSHttpClient

object LinkisClientUtils {

  private var linkisClient: DWSHttpClient = _

  def getLinkisDwsClient: DWSHttpClient = {
    if (null == linkisClient) {
      this.synchronized {
        if (null == linkisClient) {
          linkisClient = SimpleOnceJobBuilder.getLinkisManagerClient match {
            case client: LinkisManagerClient =>
              val dwsClientField = classOf[LinkisManagerClientImpl].getDeclaredField("dwsHttpClient")
              dwsClientField.setAccessible(true)
              dwsClientField.get(client).asInstanceOf[DWSHttpClient]
            case _ => null
          }
        }
      }
    }
    linkisClient
  }

}
