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
            @Param("endDate") Date endDate);

    void saveAuditLog(StreamAuditLog auditLog);
}
