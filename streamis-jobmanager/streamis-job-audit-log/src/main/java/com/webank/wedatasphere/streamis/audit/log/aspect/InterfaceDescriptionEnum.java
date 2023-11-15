package com.webank.wedatasphere.streamis.audit.log.aspect;

import org.springframework.util.AntPathMatcher;

import java.util.HashMap;
import java.util.Map;

public enum InterfaceDescriptionEnum {

    JOB_("/api/rest_j/v1/streamis/streamJobManager/job/",""),
    PROJECT_CORE_TARGET("/api/rest_j/v1/streamis/streamJobManager/project/core/target","工程核心目标"),
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
    CONFIG_JSON("/api/rest_j/v1/streamis/streamJobManager/config/json/{jobId:\\w+}","根据jobID查询/保存Job配置"),
    CONFIG_VIEW("/api/rest_j/v1/streamis/streamJobManager/config/view","查看Job配置树"),
    CONFIG_ADD("/api/rest_j/v1/streamis/streamJobManager/config/add","保存Job配置树失败"),
    CONFIG_UPDATE("/api/rest_j/v1/streamis/streamJobManager/config/update","更新Job配置树"),
    JOB_LIST("/api/rest_j/v1/streamis/streamJobManager/job/list","查询Job列表"),
    JOB_JOB_INFO("/api/rest_j/v1/streamis/streamJobManager/job/jobInfo","根据jobId查询Job详情"),
    JOB_CREATE_OR_UPDATE("/api/rest_j/v1/streamis/streamJobManager/job/createOrUpdate","创建或更新Job"),
    JOB_UPDATE_LABEL("/api/rest_j/v1/streamis/streamJobManager/job/updateLabel","更新job标签"),
    JOB_ID_VERSIONS("/api/rest_j/v1/streamis/streamJobManager/job/{jobId:\\w+}/versions","根据jobId查询Job版本列表"),
    JOB_VERSION("/api/rest_j/v1/streamis/streamJobManager/job/version","查询Job版本详细"),
    JOB_EXECUTE_INSPECT("/api/rest_j/v1/streamis/streamJobManager/job/execute/inspect","Job启动前查询"),
    JOB_EXECUTE("/api/rest_j/v1/streamis/streamJobManager/job/execute","Job启动"),
    JOB_STOP("/api/rest_j/v1/streamis/streamJobManager/job/stop","Job停止"),
    JOB_DETAILS("/api/rest_j/v1/streamis/streamJobManager/job/details","查询Job详情"),
    JOB_HISTORY("/api/rest_j/v1/streamis/streamJobManager/job/execute/history","查询Job启动历史"),
    JOB_ERROR_MSG("/api/rest_j/v1/streamis/streamJobManager/job/execute/errorMsg","查询Job错误信息"),
    JOB_ADD_TASK("/api/rest_j/v1/streamis/streamJobManager/job/addTask","添加task"),
    JOB_UPDATE_TASK("/api/rest_j/v1/streamis/streamJobManager/job/updateTask","更新task"),
    JOB_STOP_TASK("/api/rest_j/v1/streamis/streamJobManager/job/stopTask","停止task"),
    JOB_PROGRESS("/api/rest_j/v1/streamis/streamJobManager/job/progress","查看Job的进度"),
    JOB_JOB_CONTENT("/api/rest_j/v1/streamis/streamJobManager/job/jobContent","查看Job详细信息"),
    JOB_UPDATE_CONTENT("/api/rest_j/v1/streamis/streamJobManager/job/updateContent","更新Job的args或高可用信息"),
    JOB_ALERT("/api/rest_j/v1/streamis/streamJobManager/job/alert","查询Job告警信息"),
    JOB_LOGS("/api/rest_j/v1/streamis/streamJobManager/job/logs","查询Job日志"),
    JOB_STATUS("/api/rest_j/v1/streamis/streamJobManager/job/status","获得任务状态"),
    JOB_SNAPSHOT("/api/rest_j/v1/streamis/streamJobManager/job/snapshot/{jobId:\\w+}","生成Job快照"),
    JOB_UPLOAD("/api/rest_j/v1/streamis/streamJobManager/job/upload","上传Job"),
    JOB_ENABLE("/api/rest_j/v1/streamis/streamJobManager/job/enable","开启Job"),
    JOB_BAN("/api/rest_j/v1/streamis/streamJobManager/job/ban","禁用Job");
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

    public static String getDescriptionByUrl(String url) {
        if (urlDescriptionMap.containsKey(url)) {
            return urlDescriptionMap.get(url);
        }
        for (InterfaceDescriptionEnum interfaceDescription : InterfaceDescriptionEnum.values()) {
            if (matchesUrl(interfaceDescription.getUrl(), url)) {
                return interfaceDescription.getDescription();
            }
        }
        return "Unknown Description";
    }

    private static boolean matchesUrl(String pattern, String url) {
        AntPathMatcher pathMatcher = new AntPathMatcher();
        return pathMatcher.match(pattern, url);
    }
}

