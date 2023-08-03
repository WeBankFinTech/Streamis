package com.webank.wedatasphere.streamis.jobmanager.launcher.dao;

import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.StreamErrorCode;

import java.util.List;

public interface StreamErrorCodeMapper {

    List<StreamErrorCode>  getErrorCodeList();
}
