package com.webank.wedatasphere.streamis.jobmanager.log.collector.log4j2.config;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.util.Integers;

/**
 * Rpc sender configuration
 */
@Plugin(
   name = "RpcLogSender",
   category = "Core",
   printObject = true
)
public class RpcLogSenderConfig extends com.webank.wedatasphere.streamis.jobmanager.log.collector.config.RpcLogSenderConfig {

    public RpcLogSenderConfig(String address, int sendRetryCnt, int connectionTimeout, int socketTimeout, int serverRecoveryTimeInSec, int maxDelayTimeInSec,
                              RpcAuthConfig authConfig, SendLogCacheConfig cacheConfig, SendBufferConfig bufferConfig,RpcHeartbeatConfig rpcHeartbeatConfig) {
        super(address, sendRetryCnt, connectionTimeout, socketTimeout, serverRecoveryTimeInSec, maxDelayTimeInSec, authConfig, cacheConfig, bufferConfig,rpcHeartbeatConfig);
    }

    @PluginFactory
    public static RpcLogSenderConfig createConfig(
            @PluginAttribute("address") String address, @PluginAttribute("sendRetryCnt") String sendRetryCnt,
            @PluginAttribute("connectionTimeout") String connectionTimeout, @PluginAttribute("socketTimeout") String socketTimeout,
            @PluginAttribute("serverRecoveryTimeInSec") String serverRecoveryTimeInSec, @PluginAttribute("maxDelayTimeInSec") String maxDelayTimeInSec,
            @PluginAttribute("debugMode")String debugMode,
            @PluginElement("AuthConfig")RpcAuthConfig authConfig, @PluginElement("SendLogCache") SendLogCacheConfig cacheConfig,
            @PluginElement("SendBuffer")SendBufferConfig bufferConfig,@PluginElement("heartbeatConfig")RpcHeartbeatConfig rpcHeartbeatConfig){
        RpcLogSenderConfig config =  new RpcLogSenderConfig(address, Integers.parseInt(sendRetryCnt, 3),
                Integers.parseInt(connectionTimeout, 3000), Integers.parseInt(socketTimeout, 15000),
                Integers.parseInt(serverRecoveryTimeInSec, 5), Integers.parseInt(maxDelayTimeInSec, 60),
                authConfig, cacheConfig, bufferConfig,rpcHeartbeatConfig);
        config.setDebugMode(Boolean.parseBoolean(debugMode));
        return config;
    }

}
