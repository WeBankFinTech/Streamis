package com.webank.wedatasphere.streamis.datasource.appconn.ref;

import com.webank.wedatasphere.dss.standard.common.entity.ref.AbstractResponseRef;
import com.webank.wedatasphere.streamis.datasource.appconn.utils.DataSourceUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * created by yangzhiyue on 2021/4/13
 * Description:
 * 将
 */
public class DatasourceImportResponseRef extends AbstractResponseRef {


    private Long streamisTableMetaId;

    private Map<String, Object> responseMap;

    private int status;

    private String errorMsg;


    private static final Logger LOGGER = LoggerFactory.getLogger(DatasourceImportResponseRef.class);


    public DatasourceImportResponseRef(String responseBody, int status, Long streamisTableMetaId) {
        super(responseBody, status);
        this.streamisTableMetaId = streamisTableMetaId;
        init();
    }

    @SuppressWarnings("unchecked")
    private void init(){
        try{
            this.responseMap = DataSourceUtils.COMMON_GSON.fromJson(responseBody, Map.class);
            this.status = (Integer) responseMap.get("status");
            this.errorMsg = (String) responseMap.get("errorMsg");
        }catch(final Exception e){
            LOGGER.error("Failed to convert [{}] to a map", responseBody);
            this.responseMap = new HashMap<>();
            this.status = -1;
            this.errorMsg = "wrong responseBody, can not convert to a map";
        }
    }


    @Override
    public Map<String, Object> toMap() {
        return this.responseMap;
    }

    @Override
    public String getErrorMsg() {
        return this.errorMsg;
    }
}
