package com.webank.wedatasphere.streamis.audit.log.aspect;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.webank.wedatasphere.streamis.audit.log.conf.JobAuditConf;
import com.webank.wedatasphere.streamis.audit.log.entity.StreamAuditLog;
import com.webank.wedatasphere.streamis.audit.log.service.AuditLogService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Aspect
@Component
@ConditionalOnProperty(name = "feature.enabled", havingValue = "true", matchIfMissing = true)
public class AuditLogAspect {

    @Autowired
    private AuditLogService auditLogService;

    private static final Logger LOG = LoggerFactory.getLogger(AuditLogAspect.class);

    private static final Pattern PROJECT_NAME_PATTERN = Pattern.compile("[?&]projectName=([^&]+)");

    private static final String PROJECT_NAME = "projectName";
    private static final String JOB_ID = "jobId";
    private static final String BULK_SUBJECT = "bulkSubject";

    @PostConstruct
    private void init() {
        LOG.info("job log audit enabled : " + JobAuditConf.AUDIT_LOG_ENABLE.getValue());
    }


    @Around("execution(* com.webank.wedatasphere.streamis.jobmanager.restful.api..*.*(..)) || execution(* com.webank.wedatasphere.streamis.projectmanager.restful.api..*.*(..))")
    public Object captureAndLogAuditLog(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest req = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String method = req.getMethod();
        String requestURI = req.getRequestURI();
        ProxyUserEntity proxyUserEntity = ModuleUserUtils.getProxyUserEntity(req, "record audit log");
        String proxyUser = proxyUserEntity.getProxyUser();
        String userName = proxyUserEntity.getUsername();

        Map<String, Object> requestParams = getRequestParamsByProceedingJoinPoint(joinPoint);
        if (requestParams.containsKey("response")) {
            requestParams.remove("response");
        }
        Object result = null;
        long startTime = System.currentTimeMillis();
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            LOG.error("Error executing method: " + joinPoint.getSignature().toShortString());
            throw e;
        }
        long costTime = System.currentTimeMillis() - startTime;
        String jobName = getJobNameFromRequest(req, requestParams, method);
        result = Optional.ofNullable(result).orElse("--");
        logAuditInformationAsync(req, requestURI, requestParams, parseObjectToString(result), proxyUser, userName, method, costTime, jobName);
        return result;
    }

    @Async
    public void logAuditInformationAsync(HttpServletRequest req, String requestURI, Map<String, Object> requestParams,
                                         String result, String proxyUser, String userName, String method, long costTimeMills,String jobName) {
        if (!JobAuditConf.AUDIT_LOG_ENABLE.getValue()) {
            return;
        }
        if (StringUtils.isNotBlank(JobAuditConf.AUDIT_LOG_URI_SKIP.getValue())) {
            boolean end = false;
            String tmpUri = "";
            for (String uri : JobAuditConf.AUDIT_LOG_URI_SKIP.getValue().split(",")) {
                if (requestURI.equalsIgnoreCase(uri)) {
                    end = true;
                    tmpUri = uri;
                    break;
                }
            }
            if (end) {
                LOG.debug("skip to store requestUri : {}, because it matches skipped uri : {}", requestURI, tmpUri);
                return;
            }
        }
        String projectName = "";
        try {
            projectName = getProjectNameFromRequest(req, requestParams, method);
        } catch (Exception e) {
            LOG.error("审计日志projectName获取失败");
            projectName = getProjectNameFromReferer(req.getHeader("Referer"));
        }
        if (projectName == null || projectName.isEmpty()) {
            projectName = getProjectNameFromReferer(req.getHeader("Referer"));
        }
        try {
            logAuditInformation(requestURI, parseObjectToString(requestParams), result, proxyUser, userName, method, projectName, costTimeMills,jobName);
        } catch (Exception e) {
            LOG.error("审计日志记录保存失败");
        }
    }


    private void logAuditInformation(String requestURI, String requestParams, String result, String proxyUser, String userName, String method, String projectName, long costTimeMills,String jobName) {
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
        auditLog.setCostTimeMills(costTimeMills);
        auditLog.setJobName(jobName);
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
                    List<Object> tmpList = (List<Object>) value;
                    if (tmpList.size() == 0) {
                        requestParams.put(paramNames[i], value);
                        break;
                    }
                    Object tmpV1 = tmpList.get(0);
                    if (MultipartFile.class.isInstance(tmpV1)) {
                    List<MultipartFile> multipartFiles = castList(value, MultipartFile.class);
                    if (multipartFiles != null) {
                        List<String> fileNames = new ArrayList<>();
                        for (MultipartFile file : multipartFiles) {
                            fileNames.add(file.getOriginalFilename());
                        }

                        requestParams.put(paramNames[i], fileNames);
                        break;
                    }
                    }
                } catch (ClassCastException e) {
                    LOG.warn("buildRequestParam: " + value + " failed. " + e.getMessage(), e);
                }
            }
            if (!paramNames[i].equalsIgnoreCase("req") && !paramNames[i].equalsIgnoreCase("request")) {
                requestParams.put(paramNames[i], value);
            }
        }
        return requestParams;
    }

    private static <T> List<T> castList(Object obj, Class<T> clazz) {
        List<T> result = new ArrayList<>();
        if (obj instanceof List<?>) {
            for (Object o : (List<?>) obj) {
                if (clazz.isInstance(o)) {
                result.add(clazz.cast(o));
                } else if (null != o) {
                    LOG.warn("discarded {}", o.getClass());
                }
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
        if (referer != null) {
            Matcher matcher = PROJECT_NAME_PATTERN.matcher(referer);
            if (matcher.find()) {
                projectName = matcher.group(1);
            }
        }
        return projectName;
    }

    private String getProjectNameFromRequest(HttpServletRequest req, Map<String, Object> requestParams, String method) {
        if ("GET".equalsIgnoreCase(method)) {
            return getProjectNameFromGetRequest(req);
        } else if ("PUT".equalsIgnoreCase(method)) {
            return getProjectNameFromPutRequest(req, requestParams);
        } else if ("POST".equalsIgnoreCase(method)) {
            return getProjectNameFromPostRequest(req, requestParams);
        }
        return "";
    }

    private String getProjectNameFromGetRequest(HttpServletRequest req) {
        if (req.getRequestURI().equals(InterfaceDescriptionEnum.CONFIG_GET_WORKSPACE_USERS.getUrl())
                || req.getRequestURI().equals(InterfaceDescriptionEnum.CONFIG_DEFINITIONS.getUrl())) {
            return "";
        }
        String projectName = req.getParameter(PROJECT_NAME);
        if (projectName == null || projectName.isEmpty()) {
            Long jobId = Long.valueOf(req.getParameter(JOB_ID));
            projectName = auditLogService.getProjectNameById(jobId);

        }
        return projectName;
    }

    private String getProjectNameFromPutRequest(HttpServletRequest req, Map<String, Object> requestParams) {
        String projectName = "";
        if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_EXECUTE_INSPECT.getUrl())) {
            String[] jobIdArray = req.getParameterValues(JOB_ID);
            List<Integer> jobIdList = Arrays.stream(jobIdArray)
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
            Integer jobId = jobIdList.get(0);
            projectName = auditLogService.getProjectNameById(Long.valueOf(jobId));
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_STATUS.getUrl())) {
            Map<String, Object> map = BDPJettyServerHelper.gson().fromJson(requestParams.get("requestMap").toString(), Map.class);
            List<Double> jobIds = (List<Double>) map.get("id_list");
            Long jobId = Math.round(jobIds.get(0));
            projectName = auditLogService.getProjectNameById(jobId);
        } else {
            String jobId = req.getParameter(JOB_ID);
            projectName = auditLogService.getProjectNameById(Long.valueOf(jobId));
        }
        return projectName;
    }

    private String getProjectNameFromPostRequest(HttpServletRequest req, Map<String, Object> requestParams) {
        String projectName = "";
        Gson gson = BDPJettyServerHelper.gson();
        if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_UPDATE_LABEL.getUrl())) {
            Map<String, Object> tasks = gson.fromJson(gson.toJson(requestParams.get("bulkUpdateLabelRequest")), Map.class);
            List<Map<String, Object>> list = gson.fromJson(tasks.get("tasks").toString(), new TypeToken<List<Map<String, Object>>>() {
            }.getType());
            String jobId = list.get(0).get("id").toString();
            projectName = auditLogService.getProjectNameById((long) Double.parseDouble(jobId));
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_EXECUTE.getUrl())) {
            Map<String, Object> map = gson.fromJson(requestParams.get("json").toString(), Map.class);
            Long jobId = (long) Double.parseDouble(map.get(JOB_ID).toString());
            projectName = auditLogService.getProjectNameById(jobId);
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_UPDATE_CONTENT.getUrl())) {
            Map<String, Object> contentRequest = gson.fromJson(gson.toJson(requestParams.get("contentRequest")), Map.class);
            Long jobId = (long) Double.parseDouble(contentRequest.get(JOB_ID).toString());
            projectName = auditLogService.getProjectNameById(jobId);
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_BULK_EXECUTION.getUrl())) {
            Map<String, Object> bulkRequest = gson.fromJson(gson.toJson(requestParams.get("execBulkRequest")), Map.class);
            List<Double> bulkSbjList = (List<Double>) bulkRequest.get(BULK_SUBJECT);
            Long jobId = Math.round(bulkSbjList.get(0));
            projectName = auditLogService.getProjectNameById(jobId);
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_BULK_PAUSE.getUrl())) {
            Map<String, Object> bulkRequest = gson.fromJson(gson.toJson(requestParams.get("pauseRequest")), Map.class);
            List<Double> bulkSbjList = (List<Double>) bulkRequest.get(BULK_SUBJECT);
            Long jobId = Math.round(bulkSbjList.get(0));
            projectName = auditLogService.getProjectNameById(jobId);
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.PROJECT_FILES_UPLOAD.getUrl())
                || req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_UPLOAD.getUrl())) {
            projectName = req.getParameter(PROJECT_NAME);
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_ENABLE.getUrl())
                || req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_BAN.getUrl())) {
            List<Long> jobIdList = (List<Long>) requestParams.get("jobIdList");
            Long jobId = jobIdList.get(0);
            projectName = auditLogService.getProjectNameById(jobId);
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_CREATE_OR_UPDATE.getUrl())) {
            Map<String, Object> metaJsonInfo = gson.fromJson(gson.toJson(requestParams.get("metaJsonInfo")), Map.class);
            projectName = metaJsonInfo.get(PROJECT_NAME).toString();
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_UPDATE_TASK.getUrl())) {
            Map<String, Object> json = gson.fromJson(gson.toJson(requestParams.get("json")), Map.class);
            projectName = json.get(PROJECT_NAME).toString();
        } else {
            projectName = requestParams.get(PROJECT_NAME).toString();
        }
        return projectName;
    }

    private String getJobNameFromRequest(HttpServletRequest req, Map<String, Object> requestParams, String method) {
        if ("GET".equalsIgnoreCase(method)) {
            return getJobNameFromGetRequest(req);
        } else if ("PUT".equalsIgnoreCase(method)) {
            return getJobNameFromPutRequest(req, requestParams);
        } else if ("POST".equalsIgnoreCase(method)) {
            return getJobNameFromPostRequest(req, requestParams);
        }
        return "--";
    }

    private String getJobNameFromGetRequest(HttpServletRequest req) {
        String jobIdStr = req.getParameter(JOB_ID);
        if (StringUtils.isNotBlank(jobIdStr)) {
            Long jobId = Long.valueOf(jobIdStr);
            return auditLogService.getJobNameById(jobId);
        } else {
            return null;
        }
    }

    private String getJobNameFromPutRequest(HttpServletRequest req, Map<String, Object> requestParams) {
        if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_EXECUTE_INSPECT.getUrl())) {
            String[] jobIdArray = req.getParameterValues(JOB_ID);
            List<Long> jobIds = Arrays.stream(jobIdArray)
                    .map(Long::valueOf)
                    .collect(Collectors.toList());
            List<String> jobNames = auditLogService.getBulkJobNameByIds(jobIds);
            return String.join(",", jobNames);
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_STATUS.getUrl())) {
            Map<String, Object> map = BDPJettyServerHelper.gson().fromJson(requestParams.get("requestMap").toString(), Map.class);
            List<Double> jobIdDouble = (List<Double>) map.get("id_list");
            List<Long> jobIds = jobIdDouble.stream().map(Math::round).collect(Collectors.toList());
            List<String> jobNames = auditLogService.getBulkJobNameByIds(jobIds);
            return String.join(",", jobNames);
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_SNAPSHOT.getUrl())){
            if (requestParams.get(JOB_ID) != null){
                String jobIdStr = requestParams.get(JOB_ID).toString();
                Long jobId = Long.valueOf(jobIdStr);
                return auditLogService.getJobNameById(jobId);
            }
            return null;
        } else {
            String jobIdStr = req.getParameter(JOB_ID);
            if (StringUtils.isNotBlank(jobIdStr)) {
                Long jobId = Long.valueOf(jobIdStr);
                return auditLogService.getJobNameById(jobId);
            }else{
                return null;
            }
        }
    }

    private String getJobNameFromPostRequest(HttpServletRequest req, Map<String, Object> requestParams) {
        String jobName = null;
        Gson gson = BDPJettyServerHelper.gson();
        if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_UPDATE_LABEL.getUrl())) {
            Map<String, Object> tasks = gson.fromJson(gson.toJson(requestParams.get("bulkUpdateLabelRequest")), Map.class);
            List<Map<String, Object>> list = gson.fromJson(tasks.get("tasks").toString(), new TypeToken<List<Map<String, Object>>>() {
            }.getType());
            String jobId = list.get(0).get("id").toString();
            jobName = auditLogService.getJobNameById((long) Double.parseDouble(jobId));
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_EXECUTE.getUrl())) {
            Map<String, Object> map = gson.fromJson(requestParams.get("json").toString(), Map.class);
            Long jobId = (long) Double.parseDouble(map.get(JOB_ID).toString());
            jobName = auditLogService.getJobNameById(jobId);
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_UPDATE_CONTENT.getUrl())) {
            Map<String, Object> contentRequest = gson.fromJson(gson.toJson(requestParams.get("contentRequest")), Map.class);
            Long jobId = (long) Double.parseDouble(contentRequest.get(JOB_ID).toString());
            jobName = auditLogService.getJobNameById(jobId);
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_BULK_EXECUTION.getUrl())) {
            Map<String, Object> bulkRequest = gson.fromJson(gson.toJson(requestParams.get("execBulkRequest")), Map.class);
            List<Double> bulkSbjList = (List<Double>) bulkRequest.get(BULK_SUBJECT);
            List<Long> jobIds = bulkSbjList.stream().map(Math::round).collect(Collectors.toList());
            List<String> jobNames = auditLogService.getBulkJobNameByIds(jobIds);
            jobName = String.join(",", jobNames);
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_BULK_PAUSE.getUrl())) {
            Map<String, Object> bulkRequest = gson.fromJson(gson.toJson(requestParams.get("pauseRequest")), Map.class);
            List<Double> bulkSbjList = (List<Double>) bulkRequest.get(BULK_SUBJECT);
            List<Long> jobIds = bulkSbjList.stream().map(Math::round).collect(Collectors.toList());
            List<String> jobNames = auditLogService.getBulkJobNameByIds(jobIds);
            jobName = String.join(",", jobNames);
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_ENABLE.getUrl())
                || req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_BAN.getUrl())) {
            List<Long> jobIds = (List<Long>) requestParams.get("jobIdList");
            List<String> jobNames = auditLogService.getBulkJobNameByIds(jobIds);
            jobName = String.join(",", jobNames);
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_CREATE_OR_UPDATE.getUrl())) {
            Map<String, Object> metaJsonInfo = gson.fromJson(gson.toJson(requestParams.get("metaJsonInfo")), Map.class);
            jobName = metaJsonInfo.get("jobName").toString();
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_UPDATE_TASK.getUrl())) {
            Map<String, Object> json = gson.fromJson(gson.toJson(requestParams.get("json")), Map.class);
            jobName = json.get("jobName").toString();
        } else {
            if (requestParams.get(JOB_ID) != null){
                String jobIdStr = requestParams.get(JOB_ID).toString();
                Long jobId = Long.valueOf(jobIdStr);
                jobName = auditLogService.getJobNameById(jobId);
            }
        }
        return jobName;
    }

    private String parseObjectToString(Object obj) {
        try {
            if (ObjectUtils.isEmpty(obj)) {
                return "--";
            } else {
                return BDPJettyServerHelper.gson().toJson(obj);
            }
        } catch (Exception e) {
            LOG.error("failed parse map to string . " + e.getMessage());
        }
        return "--";
    }
}

