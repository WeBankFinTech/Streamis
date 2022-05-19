package com.webank.wedatasphere.streamis.jobmanager.common.vo;

import com.fasterxml.jackson.annotation.JsonAlias;

public class BaseBulkRequest<T> {

    @JsonAlias("obj_mark")
    private T objectMark;

    public T getObjectMark() {
        return objectMark;
    }

    public void setObjectMark(T objectMark) {
        this.objectMark = objectMark;
    }
}
