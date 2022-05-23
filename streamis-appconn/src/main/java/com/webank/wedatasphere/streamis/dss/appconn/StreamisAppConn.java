package com.webank.wedatasphere.streamis.dss.appconn;

import com.webank.wedatasphere.dss.appconn.core.ext.ThirdlyAppConn;
import com.webank.wedatasphere.dss.appconn.core.impl.AbstractOnlySSOAppConn;
import com.webank.wedatasphere.dss.standard.app.development.standard.DevelopmentIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.streamis.dss.appconn.structure.StreamisStructureIntegrationStandard;

public class StreamisAppConn extends AbstractOnlySSOAppConn implements ThirdlyAppConn {

    private StreamisStructureIntegrationStandard structureIntegrationStandard;

    @Override
    public DevelopmentIntegrationStandard getOrCreateDevelopmentStandard() {
        return null;
    }

    @Override
    public StructureIntegrationStandard getOrCreateStructureStandard() {
        return structureIntegrationStandard;
    }

    @Override
    protected void initialize() {
        structureIntegrationStandard = new StreamisStructureIntegrationStandard();
    }
}
