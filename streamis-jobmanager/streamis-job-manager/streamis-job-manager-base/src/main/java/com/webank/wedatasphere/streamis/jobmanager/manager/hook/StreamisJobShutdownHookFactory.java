package com.webank.wedatasphere.streamis.jobmanager.manager.hook;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StreamisJobShutdownHookFactory {

    private static final Logger logger = LoggerFactory.getLogger(StreamisJobShutdownHookFactory.class);

    private static final Map<String, StreamisJobShutdownHook> hookMap = new HashMap<>();

    public static boolean registerJobShutdownHook(StreamisJobShutdownHook hook) {
        if (null == hook) {
            return false;
        }
        StreamisJobShutdownHook oldHook = hookMap.getOrDefault(hook.getName(), null);
        if (null != oldHook) {
            logger.warn("there are already a hook with name : {} and class : {}, will cover it with new hook name : {} class : {}"
                        , oldHook.getName(), oldHook.getClass().getName(), hook.getName(), hook.getClass().getName());
        } else {
            logger.info("registered a new hook name : {} and class : {}", hook.getName(), hook.getClass().getName());
        }
        hookMap.put(hook.getName(), hook);
        return true;
    }

    public static List<StreamisJobShutdownHook> getAllHooks() {
        List<StreamisJobShutdownHook> hooks = new ArrayList<>();
        hookMap.values().stream().forEach(hook -> hooks.add(hook));
        return hooks;
    }

    public static StreamisJobShutdownHook getHookByName(String hookName) {
        if (StringUtils.isNotBlank(hookName)) {
            return hookMap.getOrDefault(hookName, null);
        } else {
            return null;
        }
    }

}
