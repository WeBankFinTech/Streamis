package com.webank.wedatasphere.streamis.audit.log.aspect;

import com.webank.wedatasphere.streamis.audit.log.entity.StreamAuditLog;
import com.webank.wedatasphere.streamis.audit.log.service.AuditLogService;
import org.apache.linkis.proxy.ProxyUserEntity;
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
import java.util.Arrays;
import java.util.Date;

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
        // Capture method name and input parameters
        String methodName = joinPoint.getSignature().getName();
        Object[] methodArgs = joinPoint.getArgs();
        Object result = null;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            LOG.error("Error executing method: " + joinPoint.getSignature().toShortString());
        }
        logAuditInformationAsync(requestURI, methodArgs, result, proxyUser,userName,method);

        return result;
    }

    @Async
    public void logAuditInformationAsync(String requestURI, Object[] methodArgs, Object result, String proxyUser,String userName,String method){
        logAuditInformation(requestURI, methodArgs, result, proxyUser,userName,method);
    }


    private void logAuditInformation(String requestURI, Object[] methodArgs, Object result, String proxyUser,String userName,String method) {
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

}

