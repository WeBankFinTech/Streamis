package com.webank.wedatasphere.streamis.audit.log.aspect;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.webank.wedatasphere.streamis.audit.log.entity.StreamAuditLog;
import com.webank.wedatasphere.streamis.audit.log.service.AuditLogService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.linkis.proxy.ProxyUserEntity;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.apache.linkis.server.utils.ModuleUserUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Aspect
@Component
public class AuditLogAspect {

    @Autowired
    private AuditLogService auditLogService;

    private static final Logger LOG = LoggerFactory.getLogger(AuditLogAspect.class);

    @Around("execution(* com.webank.wedatasphere.streamis.jobmanager.restful.api..*.*(..)) || execution(* com.webank.wedatasphere.streamis.projectmanager.restful.api..*.*(..))")
    public Object captureAndLogAuditLog(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String method = req.getMethod();
        String requestURI = req.getRequestURI();
        ProxyUserEntity proxyUserEntity = ModuleUserUtils.getProxyUserEntity(req, "record audit log");
        String proxyUser = proxyUserEntity.getProxyUser();
        String userName = proxyUserEntity.getUsername();

        Map<String, Object> requestParams = getRequestParamsByProceedingJoinPoint(joinPoint);
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            LOG.error("Error executing method: " + joinPoint.getSignature().toShortString());
        }
        result = Optional.ofNullable(result).orElse("不存在出参");
        logAuditInformationAsync(req, requestURI, parseObjectToString(requestParams), parseObjectToString(result), proxyUser, userName, method);
        return result;
    }

    @Async
    public void logAuditInformationAsync(HttpServletRequest req, String requestURI, String requestParams, String result, String proxyUser, String userName, String method) {
        try {
            String projectName = getProjectNameFromReferer(req.getHeader("Referer"));
            if (projectName == null || projectName.isEmpty()) {
                projectName = getProjectNameFromRequest(req, method);
            }
            logAuditInformation(requestURI, requestParams, result, proxyUser, userName, method, projectName);
        } catch (Exception e) {
            LOG.error("审计日志处理失败");
        }
    }


    private void logAuditInformation(String requestURI, String requestParams, String result, String proxyUser, String userName, String method, String projectName) {
        String apiDesc = InterfaceDescriptionEnum.getDescriptionByUrl(requestURI);
        String clientIp = getClientIp();
        StreamAuditLog auditLog = new StreamAuditLog();
        auditLog.setApiName(requestURI);
        auditLog.setApiDesc(apiDesc);
        auditLog.setInputParameters(requestParams);
        auditLog.setOutputParameters(result);
        auditLog.setProxyUser(proxyUser);
        auditLog.setUser(userName);
        auditLog.setOperateTime(new Date());
        auditLog.setApiType(method);
        auditLog.setProjectName(projectName);
        auditLog.setClientIp(clientIp);
        auditLogService.saveAuditLog(auditLog);
    }

    /**
     * 获取入参
     *
     * @param proceedingJoinPoint
     * @return
     */
    private Map<String, Object> getRequestParamsByProceedingJoinPoint(ProceedingJoinPoint proceedingJoinPoint) {
        //参数名
        String[] paramNames = ((MethodSignature) proceedingJoinPoint.getSignature()).getParameterNames();
        //参数值
        Object[] paramValues = proceedingJoinPoint.getArgs();

        return buildRequestParam(paramNames, paramValues);
    }

    private Map<String, Object> buildRequestParam(String[] paramNames, Object[] paramValues) {
        Map<String, Object> requestParams = new HashMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            Object value = paramValues[i];
            //如果是文件对象
            if (value instanceof MultipartFile) {
                MultipartFile file = (MultipartFile) value;
                value = file.getOriginalFilename();
            }
            //如果是批量文件上传
            if (value instanceof List) {
                try {
                    List<MultipartFile> multipartFiles = castList(value, MultipartFile.class);
                    if (multipartFiles != null) {
                        List<String> fileNames = new ArrayList<>();
                        for (MultipartFile file : multipartFiles) {
                            fileNames.add(file.getOriginalFilename());
                        }

                        requestParams.put(paramNames[i], fileNames);
                        break;
                    }
                } catch (ClassCastException e) {

                }
            }
            if (!paramNames[i].equalsIgnoreCase("req")){
                requestParams.put(paramNames[i], value);
            }
        }
        return requestParams;
    }

    private static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<T>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                result.add(clazz.cast(o));
            }
            return result;
        }
        return null;
    }

    /**
     * 获取ip
     *
     * @return
     */
    private String getClientIp() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("X-Real-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }
        // 多个代理服务器时，取第一个IP地址
        int index = ipAddress.indexOf(",");
        if (index != -1) {
            ipAddress = ipAddress.substring(0, index);
        }
        return ipAddress;
    }


    private static String getProjectNameFromReferer(String referer) {
        String projectName = " ";
        Pattern PROJECT_NAME_PATTERN = Pattern.compile("[?&]projectName=([^&]+)");
        if (referer != null) {
            Matcher matcher = PROJECT_NAME_PATTERN.matcher(referer);
            if (matcher.find()) {
                projectName = matcher.group(1);
            }
        }
        return projectName;
    }

    private String getProjectNameFromRequest(HttpServletRequest req, String method) {
        if ("GET".equalsIgnoreCase(method)) {
            return getProjectNameFromGetRequest(req);
        } else if ("PUT".equalsIgnoreCase(method)) {
            return getProjectNameFromPutRequest(req);
        } else if ("POST".equalsIgnoreCase(method)) {
            return getProjectNameFromPostRequest(req);
        }
        return "";
    }

    private String getProjectNameFromGetRequest(HttpServletRequest req) {
        if (req.getRequestURI().equals(InterfaceDescriptionEnum.CONFIG_GET_WORKSPACE_USERS.getUrl())
                || req.getRequestURI().equals(InterfaceDescriptionEnum.CONFIG_DEFINITIONS.getUrl())) {
            return "";
        }
        String projectName = req.getParameter("projectName");
        if (projectName == null || projectName.isEmpty()) {
            Long jobId = Long.valueOf(req.getParameter("jobId"));
            projectName = auditLogService.getProjectNameById(jobId);

        }
        return projectName;
    }

    private String getProjectNameFromPutRequest(HttpServletRequest req) {
        String projectName = "";
        if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_EXECUTE_INSPECT.getUrl())) {
            String[] jobIdArray = req.getParameterValues("jobId");
            List<Integer> jobIdList = Arrays.stream(jobIdArray)
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
            Integer jobId = jobIdList.get(0);
            projectName = auditLogService.getProjectNameById(Long.valueOf(jobId));
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_STATUS.getUrl())) {
            String rawJson = extractRawJsonFromRequest(req);
            Map<String, Object> resultMap = parseJsonToMap(rawJson);
            try {
                List<Long> jobIds = (List<Long>) resultMap.get("id_list");
                projectName = auditLogService.getProjectNameById(Long.valueOf(jobIds.get(0)));
            } catch (Exception e) {

            }
        } else {
            String jobId = req.getParameter("jobId");
            projectName = auditLogService.getProjectNameById(Long.valueOf(jobId));
        }

        return projectName;
    }

    private String getProjectNameFromPostRequest(HttpServletRequest req) {
        String projectName = "";
        Gson gson = BDPJettyServerHelper.gson();
        String rawJson = extractRawJsonFromRequest(req);
        Map<String, Object> resultMap = parseJsonToMap(rawJson);
        if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_UPDATE_LABEL.getUrl())) {
            Object tasks = resultMap.get("tasks");
            List<Map<String, Object>> list = new Gson().fromJson(gson.toJson(tasks), new TypeToken<List<Map<String, Object>>>() {
            }.getType());
            String jobId = list.get(0).get("id").toString();
            projectName = auditLogService.getProjectNameById(Long.valueOf(jobId));
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_EXECUTE.getUrl())
                || req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_UPDATE_CONTENT.getUrl())) {
            String jobId = resultMap.get("jobId").toString();
            projectName = auditLogService.getProjectNameById(Long.valueOf(jobId));
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_BULK_EXECUTION.getUrl())
                || req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_BULK_PAUSE.getUrl())) {
            List<Long> bulkSbjList = (List<Long>) resultMap.get("bulk_sbj");
            Long jobId = bulkSbjList.get(0);
            projectName = auditLogService.getProjectNameById(Long.valueOf(jobId));
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.PROJECT_FILES_UPLOAD.getUrl())
                || req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_UPLOAD.getUrl())) {
            projectName = req.getParameter("projectName");
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_ENABLE.getUrl())
                || req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_BAN.getUrl())) {
            projectName = req.getParameter("projectName");
        } else {
            projectName = resultMap.get("projectName").toString();
        }
        return projectName;
    }

    private String extractRawJsonFromRequest(HttpServletRequest req) {
        StringBuffer data = new StringBuffer();
        String line = null;
        BufferedReader reader = null;
        try {
            reader = req.getReader();
            while (null != (line = reader.readLine())) {
                data.append(line);
            }
        } catch (Exception e) {

        }
        return data.toString();
    }

    private Map<String, Object> parseJsonToMap(String json) {
        try {
            return BDPJettyServerHelper.jacksonJson().readValue(json, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }

    private String  parseObjectToString(Object json) {
        try {
            if (ObjectUtils.isEmpty(json)){
                return "--";
            } else {
                return BDPJettyServerHelper.gson().toJson(json);
            }
        } catch (Exception e) {
           LOG.error("failed parse map to string ");
        }
        return "--";
    }
}

