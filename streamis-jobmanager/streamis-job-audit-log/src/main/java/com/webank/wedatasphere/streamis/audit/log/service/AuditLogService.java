package com.webank.wedatasphere.streamis.audit.log.service;

import com.github.pagehelper.PageInfo;
import com.webank.wedatasphere.streamis.audit.log.entity.StreamAuditLog;


import java.util.Date;
import java.util.List;

public interface AuditLogService {

    PageInfo<StreamAuditLog> searchAuditLogs(String apiName, String user, String proxyUser, Date startDate, Date endDate,String projectName,String jobName);

    void  saveAuditLog(StreamAuditLog auditLog);

    String getProjectNameById(Long jobId);

    String getJobNameById(Long jobId);

    List<String> getBulkJobNameByIds(List<Long> jobIds);
}
