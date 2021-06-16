package com.webank.wedatasphere.streamis.datasource.appconn.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;
import com.webank.wedatasphere.streamis.datasource.appconn.utils.DataSourceUtils;

import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/13
 * Description:
 */
public class DatasourceExportResponseRef extends AbstractResponseRef {



    private Map<String, Object> bmlResource;



    public DatasourceExportResponseRef(String responseBody) {
        super(responseBody, 0);
        init();
    }

    @SuppressWarnings("unchecked")
    private void init(){
        Map<String, Object> responseMap = toMap();
        this.status = (Integer) responseMap.get("status");
        if ( status != 0){
            this.errorMsg = (String) responseMap.get("message");
        }
        bmlResource = (Map<String, Object>)responseMap.get("data");
    }


    @Override
    @SuppressWarnings("unchecked")
    public Map<String, Object> toMap() {
        return DataSourceUtils.COMMON_GSON.fromJson(responseBody, Map.class);
    }


    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }
}
