package com.webank.wedatasphere.streamis.appconn.standard;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.role.RoleService;
import com.webank.wedatasphere.dss.standard.app.structure.status.AppStatusService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.streamis.appconn.service.StreamisProjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class StreamisStructureIntegrationStandard implements StructureIntegrationStandard {


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisStructureIntegrationStandard.class);

    private ProjectService projectService;

    private AppConn appConn;

    private static StreamisStructureIntegrationStandard instance;

    private AppDesc appDesc;

    private static final String NAME = "StreamisStructureIntegrationStandard";


    private StreamisStructureIntegrationStandard(AppConn appConn){
        this.appConn = appConn;
        init();
    }

    public static StreamisStructureIntegrationStandard getInstance(AppConn appConn){
        if (null == instance){
            synchronized (StreamisStructureIntegrationStandard.class){
                if (null == instance){
                    instance = new StreamisStructureIntegrationStandard(appConn);
                }
            }
        }
        return instance;
    }


    @Override
    public void init()  {
        this.projectService = new StreamisProjectService(this.appConn);
        this.projectService.setAppDesc(getAppDesc());
    }


    @Override
    public RoleService getRoleService() {
        LOGGER.warn("yet no role service implemented for streamis appconn, will return null");
        return null;
    }

    @Override
    public ProjectService getProjectService() {
        if (null == projectService){
            LOGGER.error("project Service in streamis is null");
            return null;
        }
        return this.projectService;
    }

    @Override
    public AppStatusService getAppStateService() {
        LOGGER.warn("AppStatus Service not supported in streamis yet");
        return null;
    }

    @Override
    public AppDesc getAppDesc() {
        if (appConn == null){
            LOGGER.error("appconn in streamis is null, so appDesc is null too");
            return null;
        }
        return this.appConn.getAppDesc();
    }

    @Override
    public void setAppDesc(AppDesc appDesc) {
        //to noting
        this.appDesc = appDesc;
    }



    @Override
    public void close() throws IOException {

    }

    @Override
    public String getStandardName() {
        return NAME;
    }

    @Override
    public int getGrade() {
        return 0;
    }

    @Override
    public boolean isNecessary() {
        return false;
    }
}
