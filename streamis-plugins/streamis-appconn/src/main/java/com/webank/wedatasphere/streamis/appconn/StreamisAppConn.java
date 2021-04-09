package com.webank.wedatasphere.streamis.appconn;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.appconn.core.ext.AlmightyAppConn;
import com.webank.wedatasphere.dss.standard.common.core.AppStandard;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.streamis.appconn.standard.StreamisStructureIntegrationStandard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;


/**
 * this is the StreamisAppConn for Streamis
 */
public class StreamisAppConn implements AlmightyAppConn {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisAppConn.class);

    private AppDesc appDesc;

    private final StreamisStructureIntegrationStandard streamisStructureIntegrationStandard = StreamisStructureIntegrationStandard.getInstance(this);




    @Override
    public List<AppStandard> getAppStandards() {
        return Arrays.asList(streamisStructureIntegrationStandard);
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
