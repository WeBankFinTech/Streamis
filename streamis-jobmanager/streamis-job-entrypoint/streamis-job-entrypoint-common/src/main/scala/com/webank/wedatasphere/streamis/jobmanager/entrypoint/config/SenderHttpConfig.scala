package com.webank.wedatasphere.streamis.jobmanager.entrypoint.config

class SenderHttpConfig {

  /**
   * Timeout of http request
   */
  private var requestTimeout : scala.concurrent.duration.Duration = _

  /**
   * Request url
   */
  private var url: String = _

  /**
   * Request type
   * POST/GET
   */
  private var requestType: String = _

  /**
   * Request headers
   */
  private var headers: Map[String, String] = _

  /**
   * Request body
   */
  private var requestBody: String = _




}
