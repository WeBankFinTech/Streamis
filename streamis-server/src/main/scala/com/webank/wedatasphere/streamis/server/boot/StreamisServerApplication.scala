package com.webank.wedatasphere.streamis.server.boot

import com.webank.wedatasphere.dss.common.utils.DSSMainHelper
import com.webank.wedatasphere.linkis.DataWorkCloudApplication
import com.webank.wedatasphere.linkis.common.utils.{Logging, Utils}

/**
 * created by yangzhiyue on 2021/4/30
 * Description:
 */
object StreamisServerApplication extends Logging{

  val userName:String = System.getProperty("username.name")
  val hostName:String = Utils.getComputerName

  def main(args: Array[String]): Unit = {
//    val serviceName = System.getProperty("serviceName")//ProjectConf.SERVICE_NAME.getValue
//    DSSMainHelper.formatPropertyFiles(serviceName)
//    val allArgs = args ++ DSSMainHelper.getExtraSpringOptions
//    System.setProperty("hostName", hostName)
//    System.setProperty("userName", userName)
//    info(s"Ready to start $serviceName with args: ${allArgs.toList}.")
//    println(s"Test Ready to start $serviceName with args: ${allArgs.toList}.")
    DataWorkCloudApplication.main(args)
  }

}
