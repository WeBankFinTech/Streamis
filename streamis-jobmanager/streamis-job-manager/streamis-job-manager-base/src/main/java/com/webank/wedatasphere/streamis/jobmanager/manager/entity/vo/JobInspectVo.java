package com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Job inspect vo
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public interface JobInspectVo {

    enum Types{
        VERSION, SNAPSHOT, STATUS, LIST, HIGHAVAILABLE
    }
    @JsonIgnore
    String getInspectName();


}
