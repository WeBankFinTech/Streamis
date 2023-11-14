package com.webank.wedatasphere.streamis.audit.log.aspect;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.webank.wedatasphere.streamis.audit.log.entity.StreamAuditLog;
import com.webank.wedatasphere.streamis.audit.log.service.AuditLogService;
import org.apache.linkis.proxy.ProxyUserEntity;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.apache.linkis.server.utils.ModuleUserUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
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
        String methodName = joinPoint.getSignature().getName();
        Object[] methodArgs = joinPoint.getArgs();
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            LOG.error("Error executing method: " + joinPoint.getSignature().toShortString());
        }
        logAuditInformationAsync(req,requestURI, methodArgs, result, proxyUser,userName,method);
        return result;
    }

    @Async
    public void logAuditInformationAsync(HttpServletRequest req ,String requestURI, Object[] methodArgs, Object result, String proxyUser, String userName, String method){
        String projectName = getProjectNameFromRequest(req, method);
        logAuditInformation(requestURI, methodArgs, result, proxyUser,userName,method,projectName);
    }


    private void logAuditInformation(String requestURI, Object[] methodArgs, Object result, String proxyUser,String userName,String method,String projectName) {
        String apiDesc = InterfaceDescriptionEnum.getDescriptionByUrl(requestURI);
        StreamAuditLog auditLog = new StreamAuditLog();
        auditLog.setApiName(requestURI);
        auditLog.setApiDesc(apiDesc);
        auditLog.setInputParameters(Arrays.toString(methodArgs));
        auditLog.setOutputParameters(result.toString());
        auditLog.setProxyUser(proxyUser);
        auditLog.setUser(userName);
        auditLog.setOperateTime(new Date());
        auditLog.setApiType(method);
        auditLogService.saveAuditLog(auditLog);
    }
    private String getProjectNameFromRequest(HttpServletRequest req, String method) {
        if ("GET".equalsIgnoreCase(method)) {
            return getProjectNameFromGetRequest(req);
        } else if ("PUT".equalsIgnoreCase(method)) {
            return getProjectNameFromPutRequest(req);
        } else if ("POST".equalsIgnoreCase(method)) {
            String projectName = getProjectNameFromPostRequest(req);

            return projectName;
        }
        return "";
    }

    private String getProjectNameFromGetRequest(HttpServletRequest req) {
        String projectName = req.getParameter("projectName");
        if (projectName == null || projectName.isEmpty()) {
            Long jobId = Long.valueOf(req.getParameter("jobId"));
            projectName = auditLogService.getProjectNameById(jobId);

        }
        return projectName;
    }

    private String getProjectNameFromPutRequest(HttpServletRequest req) {
        String projectName ="";
        if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_EXECUTE_INSPECT.getUrl())){
            String[] jobIdArray = req.getParameterValues("jobId");
            List<Integer> jobIdList = Arrays.stream(jobIdArray)
                    .map(Integer::valueOf)
                    .collect(Collectors.toList());
            Integer jobId = jobIdList.get(0);
            projectName = auditLogService.getProjectNameById(Long.valueOf(jobId));
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_STATUS.getUrl())){
            String rawJson = extractRawJsonFromRequest(req);
            Map<String, Object> resultMap = parseJsonToMap(rawJson);
            try {
                List<Long> jobIds = (List<Long>) resultMap.get("id_list");
                projectName = auditLogService.getProjectNameById(Long.valueOf(jobIds.get(0)));
            } catch (Exception e){

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
        if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_UPDATE_LABEL.getUrl())){
            Object tasks = resultMap.get("tasks");
            List<Map<String, Object>> list = new Gson().fromJson(gson.toJson(tasks), new TypeToken<List<Map<String, Object>>>(){}.getType());
            String jobId = list.get(0).get("id").toString();
            projectName =auditLogService.getProjectNameById(Long.valueOf(jobId));
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_EXECUTE.getUrl())
            || req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_UPDATE_CONTENT.getUrl()) ){
            String jobId = resultMap.get("jobId").toString();
            projectName =auditLogService.getProjectNameById(Long.valueOf(jobId));
        } else if (req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_BULK_EXECUTION.getUrl())
                || req.getRequestURI().equals(InterfaceDescriptionEnum.JOB_BULK_PAUSE.getUrl())) {
            List<Long> bulkSbjList = (List<Long>) resultMap.get("bulk_sbj");
            Long jobId = bulkSbjList.get(0);
            projectName =auditLogService.getProjectNameById(Long.valueOf(jobId));
        } else {
             projectName = resultMap.get("projectName").toString();
        }
        return projectName;
    }

    private String extractRawJsonFromRequest(HttpServletRequest request) {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;
        try {
            InputStream inputStream = request.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭 BufferedReader
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return stringBuilder.toString();
    }

    private Map<String, Object> parseJsonToMap(String json) {
        try {
            return BDPJettyServerHelper.jacksonJson().readValue(json, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }
}

