package com.webank.wedatasphere.streamis.jobmanager.vo;

import java.util.List;

public class BulkUpdateLabelResponse {
    private List<BulkUpdateLabel>  data;

    public List<BulkUpdateLabel> getData() {
        return data;
    }

    public void setData(List<BulkUpdateLabel> data) {
        this.data = data;
    }
}
