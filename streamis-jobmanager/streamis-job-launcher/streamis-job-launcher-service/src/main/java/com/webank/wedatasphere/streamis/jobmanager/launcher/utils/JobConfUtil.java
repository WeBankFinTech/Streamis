package com.webank.wedatasphere.streamis.jobmanager.launcher.utils;

import com.webank.wedatasphere.streamis.jobmanager.launcher.conf.JobConfKeyConstants;
import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.JobConfValue;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class JobConfUtil {

    private JobConfUtil(){}

    public static List<JobConfValue> valuesHandler (List<JobConfValue> jobConfValueList){
        String manageMode = JobConfKeyConstants.MANAGE_MODE_KEY().getValue();
        for (JobConfValue jobConfValue : jobConfValueList) {
            if (jobConfValue.getKey().equals(manageMode)) {
                if (jobConfValue.getValue().isEmpty()) {
                    jobConfValue.setValue("detach");
                    break;
                }
            }
        }
        return jobConfValueList;
    }


}
