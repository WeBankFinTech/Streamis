package com.webank.wedatasphere.streamis.appconn;

import com.google.common.collect.Lists;
import com.webank.wedatasphere.dss.appconn.core.ext.AlmightyAppConn;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.streamis.appconn.standard.StreamisDevelopmentIntegrationStandard;
import com.webank.wedatasphere.streamis.appconn.standard.StreamisStructureIntegrationStandard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * this is the StreamisAppConn for Streamis
 */
public class StreamisAppConn implements AlmightyAppConn {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisAppConn.class);

    private AppDesc appDesc;

    private final StreamisStructureIntegrationStandard streamisStructureIntegrationStandard = StreamisStructureIntegrationStandard.getInstance(this);

    private final StreamisDevelopmentIntegrationStandard developmentIntegrationStandard = StreamisDevelopmentIntegrationStandard.getInstance(this);


    private List<AppStandard> appStandards;


    @Override
    public List<AppStandard> getAppStandards() {
        if (appStandards == null) {
            synchronized (StreamisAppConn.class) {
                if (appStandards == null) {
                    appStandards = Lists.newArrayList(streamisStructureIntegrationStandard, developmentIntegrationStandard);
                }
            }
        }
        return appStandards;
    }

    @Override
    public AppDesc getAppDesc() {
        return this.appDesc;
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        this.appDesc = appDesc;
    }
}
