package com.webank.wedatasphere.streamis.jobmanager.manager.conf;

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.utils.JobUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.linkis.common.conf.CommonVars;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class JobManagerConf {

    private static final Logger logger = LoggerFactory.getLogger(JobManagerConf.class);

    private static Map<String, List<String>> hookProjectMap = null;

    public static final CommonVars<Boolean> ENABLE_JOB_SHUTDOWN_HOOKS = CommonVars.apply("wds.streamis.job.shutdown.hook.enable", true);


    /*
    eg: wds.streamis.job.shutdown.hooks=hook1=project1,project2;hook2=project1,project2
     */
    public static final CommonVars<String> JOB_SHUTDOWN_HOOKS = CommonVars.apply("wds.streamis.job.shutdown.hooks", "");

    public static final CommonVars<Long> JOB_SHUTDOWN_HOOK_TIMEOUT_MILLS = CommonVars.apply("wds.streamis.job.shutdown.hook.timeout.mills", 5 * 60 * 1000L);

    private static void initHookMap() {
        hookProjectMap = new HashMap<>();
        String hookConfig = JOB_SHUTDOWN_HOOKS.getValue();
        if (StringUtils.isNotBlank(hookConfig)) {
            for (String hooksAndProjects : hookConfig.split(";")) {
                if (hooksAndProjects.indexOf("=") > -1) {
                    String[] hookProjectArr = hooksAndProjects.split("=");
                    String hookName = hookProjectArr[0];
                    List<String> projectList = new ArrayList<>();
                    if (hookProjectArr.length == 2) {
                        String projects = hookProjectArr[1];
                        String[] projectArr = projects.split(",");
                        projectList.addAll(Arrays.asList(projectArr));
                    } else {
                        logger.warn("Invalid hook : {} config {}={}", hookName, JOB_SHUTDOWN_HOOKS.key(), JOB_SHUTDOWN_HOOKS.getValue());
                    }
                    if (hookProjectMap.containsKey(hookName)) {
                        logger.warn("hook : {} projects : {} will be replaced by {}", hookName, JobUtils.gson().toJson(hookProjectMap.get(hookName)), JobUtils.gson().toJson(projectList));
                    }
                    hookProjectMap.put(hookName, projectList);
                } else {
                    logger.warn("Invalid config {}={}", JOB_SHUTDOWN_HOOKS.key(), JOB_SHUTDOWN_HOOKS.getValue());
                }
            }
        }
    }

    public static List<String> getHookNames() {
        List<String> names = new ArrayList<>();
        if (ENABLE_JOB_SHUTDOWN_HOOKS.getValue() && null == hookProjectMap) {
                initHookMap();
            }
        for (String name : hookProjectMap.keySet()) {
            names.add(name);
        }
        return names;
    }

    public static List<String> getHooksByProject(String projectName) {
        List<String> hookList = new ArrayList<>();
        if (ENABLE_JOB_SHUTDOWN_HOOKS.getValue() && StringUtils.isNotBlank(projectName)) {
            for (Map.Entry<String, List<String>> entry : hookProjectMap.entrySet()) {
                for (String project : entry.getValue()) {
                    if (projectName.equalsIgnoreCase(project)) {
                        hookList.add(entry.getKey());
                    }
                }
            }
        }
        return hookList;
    }

    public static List<String> getProjectByHook(String hookName) {
        if (ENABLE_JOB_SHUTDOWN_HOOKS.getValue()) {
            return hookProjectMap.getOrDefault(hookName, new ArrayList<>());
        } else {
            return new ArrayList<>();
        }
    }
}
