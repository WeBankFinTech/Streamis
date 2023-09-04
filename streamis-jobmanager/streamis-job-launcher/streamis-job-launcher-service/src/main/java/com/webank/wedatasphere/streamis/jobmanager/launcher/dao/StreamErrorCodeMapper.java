package com.webank.wedatasphere.streamis.jobmanager.launcher.dao;



import com.webank.wedatasphere.streamis.jobmanager.launcher.entity.StreamErrorCode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface StreamErrorCodeMapper {

    List<StreamErrorCode>  getErrorCodeList();
}
