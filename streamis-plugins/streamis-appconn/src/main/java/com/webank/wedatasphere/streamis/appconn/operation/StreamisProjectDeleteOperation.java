package com.webank.wedatasphere.streamis.appconn.operation;

import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectDeletionOperation;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by yangzhiyue on 2021/4/25
 * Description:
 */
public class StreamisProjectDeleteOperation implements ProjectDeletionOperation {


    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisProjectDeleteOperation.class);

    //todo 以后写，先等下写

    @Override
    public ProjectResponseRef deleteProject(ProjectRequestRef projectRequestRef) throws ExternalOperationFailedException {
        return null;
    }

    @Override
    public void setStructureService(StructureService structureService) {

    }
}
