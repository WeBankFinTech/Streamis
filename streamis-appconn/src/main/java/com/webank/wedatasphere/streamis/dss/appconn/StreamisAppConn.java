package com.webank.wedatasphere.streamis.dss.appconn;

import com.webank.wedatasphere.dss.appconn.core.ext.ThirdlyAppConn;
import com.webank.wedatasphere.dss.appconn.core.impl.AbstractOnlySSOAppConn;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;

public class StreamisAppConn extends AbstractOnlySSOAppConn implements ThirdlyAppConn {
    @Override
    public DevelopmentIntegrationStandard getOrCreateDevelopmentStandard() {
        // Not to use the development flow, so return null
        return null;
    }

    @Override
    public StructureIntegrationStandard getOrCreateStructureStandard() {

        return null;
    }

    @Override
    protected void initialize() {

    }
}
