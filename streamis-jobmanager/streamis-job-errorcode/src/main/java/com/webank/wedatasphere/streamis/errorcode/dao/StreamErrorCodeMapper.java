package com.webank.wedatasphere.streamis.errorcode.dao;



import com.webank.wedatasphere.streamis.errorcode.entity.StreamErrorCode;

import java.util.List;

public interface StreamErrorCodeMapper {

    List<StreamErrorCode>  getErrorCodeList();
}
