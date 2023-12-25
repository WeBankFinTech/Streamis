package com.webank.wedatasphere.streamis.jobmanager.log.collector.log4j2.config;

import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

public class RpcHeartbeatConfig extends com.webank.wedatasphere.streamis.jobmanager.log.collector.config.RpcHeartbeatConfig{

    public RpcHeartbeatConfig(){
        super();
    }
    public RpcHeartbeatConfig(String heartbeatAddress, int heartbeatInterval) {
        super(heartbeatAddress, heartbeatInterval);
    }

    @PluginFactory
    public static RpcHeartbeatConfig createRpcHeartbeatConfig(@PluginAttribute("heartbeatAddress") String heartbeatAddress,
                                                    @PluginAttribute("heartbeatInterval") int heartbeatInterval){
        return new RpcHeartbeatConfig(heartbeatAddress, heartbeatInterval);
    }


}
