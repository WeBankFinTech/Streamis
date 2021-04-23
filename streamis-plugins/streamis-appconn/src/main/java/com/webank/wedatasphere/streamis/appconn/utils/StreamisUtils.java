package com.webank.wedatasphere.streamis.appconn.utils;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.http.client.HttpClient;

/**
 * A tool class for streamis
 * such as http etc
 */
public class StreamisUtils {



    private HttpClient httpClient;



    public static final Gson COMMON_GSON = new GsonBuilder().disableHtmlEscaping().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();






}
