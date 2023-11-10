package com.webank.wedatasphere.streamis.audit.log.aspect;

import java.util.HashMap;
import java.util.Map;

public enum InterfaceDescriptionEnum {

    default34("/api/rest_j/v1/streamis/streamJobManager/config",""),
    PROJECT_FILES_UPLOAD( "/api/rest_j/v1/streamis/streamProjectManager/project/files/upload", "上传工程资源文件"),
    PROJECT_FILES_LIST("/api/rest_j/v1/streamis/streamProjectManager/project/files/list","查询工程资源文件"),
    PROJECT_FILES_VERSION_LIST("/api/rest_j/v1/streamis/streamProjectManager/project/files/version/list","查询工程资源文件版本信息"),
    PROJECT_FILES_DELETE("/api/rest_j/v1/streamis/streamProjectManager/project/files/delete","删除工程资源文件"),
    PROJECT_FILES_VERSION_DELETE("/api/rest_j/v1/streamis/streamProjectManager/project/files/version/delete","删除工程资源文件版本"),
    PROJECT_FILES_DOWNLOAD("/api/rest_j/v1/streamis/streamProjectManager/project/files/download","下载工程资源文件"),
    JOB_BULK_EXECUTION("/api/rest_j/v1/streamis/streamJobManager/job/bulk/execution","job批量启动"),
    JOB_BULK_PAUSE("/api/rest_j/v1/streamis/streamJobManager/job/bulk/pause","job批量停止"),
    CONFIG_GET_WORKSPACE_USERS("/api/rest_j/v1/streamis/streamJobManager/config/getWorkspaceUsers","获取工作空间用户"),
    CONFIG_DEFINITIONS("/api/rest_j/v1/streamis/streamJobManager/config/definitions","查询配置定义"),

    JOBCONTENT("/jobContent", "查看job详情");

    private String url;
    private String description;


    private static  String systemPath = "/api/rest_j/v1";

    private static  String projectManagerRestfulApiPath = "/streamis/streamProjectManager/project";


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

