package com.webank.wedatasphere.streamis.jobmanager.manager.hook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JobShutdownHookFactory {

    private static final Logger logger = LoggerFactory.getLogger(JobShutdownHookFactory.class);

    private static final Map<String, JobShutdownHook> hookMap = new HashMap<String, JobShutdownHook>();

    public static boolean registerJobShutdownHook(JobShutdownHook hook) {
        if (null == hook) {
            return false;
        }
        JobShutdownHook oldHook = hookMap.getOrDefault(hook.getName(), null);
        if (null != oldHook) {
            logger.warn("there are already a hook with name : {} and class : {}, will cover it with new hook name : {} class : {}"
                        , oldHook.getName(), oldHook.getClass().getName(), hook.getName(), hook.getClass().getName());
        } else {
            logger.info("registered a new hook name : {} and class : {}", hook.getName(), hook.getClass().getName());
        }
        hookMap.put(hook.getName(), hook);
        return true;
    }

    public static List<JobShutdownHook> getAllHooks() {
        List<JobShutdownHook> hooks = new ArrayList<>();
        hookMap.values().stream().forEach(hook -> hooks.add(hook));
        return hooks;
    }

}
