package com.webank.wedatasphere.streamis.jobmanager.exception;

import java.util.HashMap;
import java.util.Map;

public class JobExceptionManager {
    //30300-30599
    private static Map<String, String> desc = new HashMap<String, String>(32) {
        {
            put("30300", "上传失败");
            put("30301","%s cannot be empty!");

        }
    };

    public static JobException createException(int errorCode, Object... format) throws JobException {
        return new JobException(errorCode, String.format(desc.get(String.valueOf(errorCode)), format));
    }
}
