package com.webank.wedatasphere.streamis.jobmanager.manager.utils;

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamJob;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Stack;

public class MergeUtils {
    public static void merge(Map<String, Object> destination, Map<String, Object> source) {
        Stack<Map<String, Object>> stackDest = new Stack<>();
        Stack<Map<String, Object>> stackSrc = new Stack<>();

        stackDest.push(destination);
        stackSrc.push(source);

        while (!stackSrc.isEmpty()) {
            Map<String, Object> currentDest = stackDest.pop();
            Map<String, Object> currentSrc = stackSrc.pop();

            for (String key : currentSrc.keySet()) {
                Object srcValue = currentSrc.get(key);
                if (currentDest.containsKey(key) && currentDest.get(key) instanceof Map && srcValue instanceof Map) {
                    stackDest.push((Map<String, Object>) currentDest.get(key));
                    stackSrc.push((Map<String, Object>) srcValue);
                } else {
                    // 如果不是两个Map，或键在目标中不存在，直接设置或覆盖
                    currentDest.put(key, srcValue);
                }
            }
        }
    }
    public static Map<String,Object> getJobTemplateConfMap(String jobTemplate){
        Map<String,Object> jobTemplateConfig = null;
        if(StringUtils.isNotBlank(jobTemplate)){
            Map<String,Object> jobTemplateMap = JobContentUtils.getMap(jobTemplate);
            if (jobTemplateMap.containsKey("jobConfig") && jobTemplateMap.get("jobConfig") instanceof Map<?, ?>) {
                jobTemplateConfig = (Map<String, Object>) jobTemplateMap.get("jobConfig");
            }
        }
        return jobTemplateConfig;
    }

}