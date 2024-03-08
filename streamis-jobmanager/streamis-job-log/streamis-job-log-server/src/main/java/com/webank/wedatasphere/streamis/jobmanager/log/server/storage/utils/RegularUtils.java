package com.webank.wedatasphere.streamis.jobmanager.log.server.storage.utils;

public class RegularUtils {

    private RegularUtils(){}

    public static String[] split(String str){
        return str.split("\\.");
    }
}
