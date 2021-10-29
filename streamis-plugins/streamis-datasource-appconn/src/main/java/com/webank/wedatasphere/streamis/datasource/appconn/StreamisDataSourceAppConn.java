package com.webank.wedatasphere.streamis.datasource.appconn;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * created by yangzhiyue on 2021/4/7
 * Description: the main appconn of streamis datasource
 * it will be used in streamisJobManagerAppConn
 */
public class StreamisDataSourceAppConn implements AppConn {


    private AppDesc appDesc;


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisDataSourceAppConn.class);


    @Override
    public List<AppStandard> getAppStandards() {
        return null;
    }

    @Override
    public AppDesc getAppDesc() {
        if (appDesc == null){
            LOGGER.error("AppDesc in streamisDatasourceAppConn is null, it means appconn did not init correctly");
        }
        return appDesc;
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }
}
