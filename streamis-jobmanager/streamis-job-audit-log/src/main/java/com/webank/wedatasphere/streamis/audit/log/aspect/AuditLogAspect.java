package com.webank.wedatasphere.streamis.audit.log.aspect;

import com.webank.wedatasphere.streamis.audit.log.entity.StreamAuditLog;
import com.webank.wedatasphere.streamis.audit.log.service.AuditLogService;
import org.apache.linkis.proxy.ProxyUserEntity;
import org.apache.linkis.server.utils.ModuleUserUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;

@Aspect
@Component
public class AuditLogAspect {

    @Autowired
    private AuditLogService auditLogService;

    @Around("execution(* com.webank.wedatasphere.streamis.jobmanager.restful.api..*.*(..)) || execution(* com.webank.wedatasphere.streamis.projectmanager.restful.api..*.*(..))")
    public Object captureAndLogAuditLog(ProceedingJoinPoint joinPoint, HttpServletRequest req) throws Throwable {
        ProxyUserEntity proxyUserEntity = ModuleUserUtils.getProxyUserEntity(req, "record audit log");
        String proxyUser = proxyUserEntity.getProxyUser();
        String userName = proxyUserEntity.getUsername();
        // Capture method name and input parameters
        String methodName = joinPoint.getSignature().getName();
        Object[] methodArgs = joinPoint.getArgs();

        Object result = joinPoint.proceed();

        logAuditInformation(methodName, methodArgs, result, proxyUser,userName);

        return result;
    }

    private void logAuditInformation(String methodName, Object[] methodArgs, Object result, String proxyUser,String userName) {
        // Create and save audit log using AuditLogService
        StreamAuditLog auditLog = new StreamAuditLog();
        auditLog.setApiName(methodName);
        auditLog.setInputParameters(Arrays.toString(methodArgs));
        auditLog.setOutputParameters(result.toString());
        auditLog.setProxyUser(proxyUser);
        auditLog.setUser(userName);
        auditLog.setOperateTime(new Date());
        auditLogService.saveAuditLog(auditLog);
    }

}

