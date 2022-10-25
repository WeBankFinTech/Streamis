package com.webank.wedatasphere.streamis.jobmanager.log.collector.log4j2.config;

import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

/**
 * AuthConfig Element in log4j2
 */
@Plugin(
        name = "AuthConfig",
        category = "Core",
        printObject = true
)
public class RpcAuthConfig extends com.webank.wedatasphere.streamis.jobmanager.log.collector.config.RpcAuthConfig {

    public RpcAuthConfig(){
        super();
    }
    public RpcAuthConfig(String tokenCodeKey, String tokenCode, String tokenUserKey, String tokenUser) {
        super(tokenCodeKey, tokenCode, tokenUserKey, tokenUser);
    }

    @PluginFactory
    public static RpcAuthConfig createRpcAuthConfig(@PluginAttribute("tokenCodeKey") String tokenCodeKey,
                                                    @PluginAttribute("tokenCode") String tokenCode,
                                                    @PluginAttribute("tokenUserKey") String tokenUserKey, @PluginAttribute("tokenUser") String tokenUser){
        return new RpcAuthConfig(tokenCodeKey, tokenCode, tokenUserKey, tokenUser);
    }

}
