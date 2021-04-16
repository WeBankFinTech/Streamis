package com.webank.wedatasphere.streamis.datasource.appconn.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webank.wedatasphere.linkis.common.conf.CommonVars;

/**
 * created by yangzhiyue on 2021/4/8
 * Description:
 */
public class DataSourceUtils {

    public static final Gson COMMON_GSON = new GsonBuilder().disableHtmlEscaping().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();


    public static final CommonVars<String> DATASOURCE_APP_NAME = CommonVars.apply("wds.streamis.datasource.app.name", "streamis");

    public static final String DEV = "dev";

    public static final String PROD = "prod";





}
