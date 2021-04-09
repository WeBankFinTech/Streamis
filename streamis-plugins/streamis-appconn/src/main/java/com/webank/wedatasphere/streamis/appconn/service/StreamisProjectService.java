package com.webank.wedatasphere.streamis.appconn.service;

import com.webank.wedatasphere.dss.appconn.core.AppConn;
import com.webank.wedatasphere.dss.standard.app.structure.StructureIntegrationStandard;
import com.webank.wedatasphere.dss.standard.app.structure.project.*;
import com.webank.wedatasphere.dss.standard.common.app.AppIntegrationService;
import com.webank.wedatasphere.dss.standard.common.desc.AppDesc;
import com.webank.wedatasphere.dss.standard.common.desc.AppInstance;
import com.webank.wedatasphere.dss.standard.common.service.Operation;
import com.webank.wedatasphere.streamis.appconn.operation.StreamisProjectCreationOperation;
import com.webank.wedatasphere.streamis.appconn.operation.StreamisProjectUpdateOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StreamisProjectService implements ProjectService {


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisProjectService.class);

    private AppInstance appInstance;

    private AppConn appConn;


    public StreamisProjectService(AppConn appConn){
        this.appConn = appConn;
    }


    private final ProjectCreationOperation projectCreationOperation = new StreamisProjectCreationOperation(this);

    private final ProjectUpdateOperation projectUpdateOperation = new StreamisProjectUpdateOperation(this);



    @Override
    public ProjectCreationOperation createProjectCreationOperation() {
        return this.projectCreationOperation;
    }

    @Override
    public ProjectUpdateOperation createProjectUpdateOperation() {
        return this.projectUpdateOperation;
    }

    @Override
    public ProjectDeletionOperation createProjectDeletionOperation() {
        return null;
    }

    @Override
    public ProjectUrlOperation createProjectUrlOperation() {
        return null;
    }

    @Override
    public void setSSOService(AppIntegrationService appIntegrationService) {

    }

    @Override
    public AppIntegrationService getSSOService() {
        return null;
    }

    @Override
    public void setAppStandard(StructureIntegrationStandard structureIntegrationStandard) {

    }

    @Override
    public StructureIntegrationStandard getAppStandard() {
        return null;
    }

    @Override
    public AppInstance getAppInstance() {
        return null;
    }

    @Override
    public void setAppInstance(AppInstance appInstance) {

    }

    @Override
    public void setAppDesc(AppDesc appDesc) {

    }

    @Override
    public AppDesc getAppDesc() {
        return null;
    }

    @Override
    public Operation createOperation(Class<? extends Operation> aClass) {
        return null;
    }

    @Override
    public boolean isOperationExists(Class<? extends Operation> aClass) {
        return false;
    }

    @Override
    public boolean isOperationNecessary(Class<? extends Operation> aClass) {
        return false;
    }

    @Override
    public boolean isCooperationSupported() {
        return false;
    }

    @Override
    public boolean isProjectNameUnique() {
        return false;
    }
}
