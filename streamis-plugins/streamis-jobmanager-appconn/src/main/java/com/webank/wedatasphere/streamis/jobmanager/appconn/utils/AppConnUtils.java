package com.webank.wedatasphere.streamis.jobmanager.appconn.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * created by yangzhiyue on 2021/4/15
 * Description:
 */
public class AppConnUtils {

    public static final Gson COMMON_GSON = new GsonBuilder().disableHtmlEscaping().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();

    public static final String STREAMIS_NAME = "Streamis";

}
