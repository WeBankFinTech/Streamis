package com.webank.wedatasphere.streamis.audit.log.aspect;

import java.util.HashMap;
import java.util.Map;

public enum InterfaceDescriptionEnum {

    LIST("/list", "分页查找所有job"),
    JOBCONTENT("/jobContent", "查看job详情");

    private String url;
    private String description;

    InterfaceDescriptionEnum(String url, String description) {
        this.url = url;
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    private static final Map<String, String> urlDescriptionMap = new HashMap<>();

    static {
        for (InterfaceDescriptionEnum interfaceDescription : InterfaceDescriptionEnum.values()) {
            urlDescriptionMap.put(interfaceDescription.getUrl(), interfaceDescription.getDescription());
        }
    }

    public static Map<String, String> getUrlDescriptionMap() {
        return new HashMap<>(urlDescriptionMap);
    }
}

