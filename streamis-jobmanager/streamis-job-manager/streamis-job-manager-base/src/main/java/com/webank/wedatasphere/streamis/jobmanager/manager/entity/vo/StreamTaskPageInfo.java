package com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo;

import java.util.List;

public class StreamTaskPageInfo {
    private List<StreamTaskListVo>  streamTaskList;

    private Integer total;


    public List<StreamTaskListVo> getStreamTaskList() {
        return streamTaskList;
    }

    public void setStreamTaskList(List<StreamTaskListVo> streamTaskList) {
        this.streamTaskList = streamTaskList;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }
}
