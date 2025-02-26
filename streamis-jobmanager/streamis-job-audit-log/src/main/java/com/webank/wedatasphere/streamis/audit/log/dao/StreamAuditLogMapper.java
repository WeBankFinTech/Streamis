package com.webank.wedatasphere.streamis.audit.log.dao;

import com.webank.wedatasphere.streamis.audit.log.entity.StreamAuditLog;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface StreamAuditLogMapper {

    List<StreamAuditLog> searchAuditLogs(
            @Param("apiName") String apiName,
            @Param("user") String user,
            @Param("proxyUser") String proxyUser,
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate,
            @Param("projectName") String projectName,
            @Param("jobName") String jobName,
            @Param("jobNameIsNull") Boolean jobNameIsNull,
            @Param("jobNameLike") String jobNameLike);

    void saveAuditLog(StreamAuditLog auditLog);

    String getProjectNameById(@Param("jobId")  Long jobId);

    String getJobNameById(@Param("jobId")  Long jobId);

    List<String> getBulkJobNameByIds(@Param("jobIds")  List<Long> jobIds);
}
