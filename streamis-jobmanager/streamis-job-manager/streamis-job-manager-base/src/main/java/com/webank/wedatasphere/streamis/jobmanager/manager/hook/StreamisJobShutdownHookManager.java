package com.webank.wedatasphere.streamis.jobmanager.manager.hook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


public class StreamisJobShutdownHookManager {

    private static final Logger logger = LoggerFactory.getLogger(StreamisJobShutdownHookManager.class);

    private static Map<String, StreamisJobShutdowHook> hooks = new HashMap<String, StreamisJobShutdowHook>();

    public boolean registerShutdownHook(StreamisJobShutdowHook hook) {
        if (null != hook) {
            if (hooks.containsKey(hook.getClass().getCanonicalName())) {
                logger.warn("hook with calss : {} has been registered, cannot register again.");
                return false;
            } else {
                hooks.put(hook.getClass().getCanonicalName(), hook);
                logger.info("succeed to register hook with class : {}", hook.getClass().getCanonicalName());
                return true;
            }
        }
        logger.error("Invalid null hook of StreamisJobShutdownHook.");
        return false;
    }

}
