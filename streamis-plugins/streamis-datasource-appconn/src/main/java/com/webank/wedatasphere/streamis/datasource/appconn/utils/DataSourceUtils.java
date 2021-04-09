package com.webank.wedatasphere.streamis.datasource.appconn.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * created by yangzhiyue on 2021/4/8
 * Description:
 */
public class DataSourceUtils {

    public static final Gson COMMON_GSON = new GsonBuilder().disableHtmlEscaping().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();


}
