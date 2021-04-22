package com.webank.wedatasphere.streamis.datasource.transfer.client

import java.util.concurrent.TimeUnit

import com.webank.wedatasphere.linkis.datasource.client.impl.LinkisDataSourceRemoteClient
import com.webank.wedatasphere.linkis.datasource.client.request.QueryDataSourceAction
import com.webank.wedatasphere.linkis.datasource.client.response.QueryDataSourceResult
import com.webank.wedatasphere.linkis.httpclient.dws.authentication.StaticAuthenticationStrategy
import com.webank.wedatasphere.linkis.httpclient.dws.config.DWSClientConfigBuilder
import com.webank.wedatasphere.streamis.datasource.transfer.conf.TransferConfiguration

object LinkisDataSourceClient {
  //Linkis Datasource Client Config
  val serverUrl = TransferConfiguration.SERVER_URL.getValue
  val connectionTimeout = TransferConfiguration.CONNECTION_TIMEOUT.getValue
  val discoveryEnabled = TransferConfiguration.DISCOVERY_ENABLED.getValue
  val discoveryFrequencyPeriod = TransferConfiguration.DISCOVERY_FREQUENCY_PERIOD.getValue
  val loadbalancerEnabled = TransferConfiguration.LOAD_BALANCER_ENABLED.getValue
  val maxConnectionSize = TransferConfiguration.MAX_CONNECTION_SIZE.getValue
  val retryEnabled = TransferConfiguration.RETRY_ENABLED.getValue
  val readTimeout = TransferConfiguration.READ_TIMEOUT.getValue
  val authTokenKey = TransferConfiguration.AUTHTOKEN_KEY.getValue
  val authTokenValue = TransferConfiguration.AUTHTOKEN_VALUE.getValue
  val dwsVersion = TransferConfiguration.DWS_VERSION.getValue


  val clientConfig = DWSClientConfigBuilder.newBuilder()
    .addServerUrl(serverUrl)
    .connectionTimeout(connectionTimeout)
    .discoveryEnabled(discoveryEnabled)
    .discoveryFrequency(1,TimeUnit.MINUTES)
    .loadbalancerEnabled(loadbalancerEnabled)
    .maxConnectionSize(maxConnectionSize)
    .retryEnabled(retryEnabled)
    .readTimeout(readTimeout)
    .setAuthenticationStrategy(new StaticAuthenticationStrategy())
    .setAuthTokenKey(authTokenKey)
    .setAuthTokenValue(authTokenValue)
    .setDWSVersion(dwsVersion)
    .build()

  val dataSourceClient = new LinkisDataSourceRemoteClient(clientConfig)

  def queryDataSource(linkisDatasourceName:String) : QueryDataSourceResult = {
    dataSourceClient.queryDataSource(QueryDataSourceAction.builder()
      .setSystem("")
      .setName(linkisDatasourceName)
      .setTypeId(1)
      .setIdentifies("")
      .setCurrentPage(1)
      .setPageSize(1).build()
    )
  }

}
