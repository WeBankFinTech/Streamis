package com.webank.wedatasphere.streamis.appconn.operation;

import com.webank.wedatasphere.dss.standard.app.structure.StructureService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectRequestRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectResponseRef;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectService;
import com.webank.wedatasphere.dss.standard.app.structure.project.ProjectUpdateOperation;
import com.webank.wedatasphere.dss.standard.common.exception.operation.ExternalOperationFailedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by yangzhiyue on 2021/4/7
 * Description:
 */
public class StreamisProjectUpdateOperation implements ProjectUpdateOperation {



    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisProjectUpdateOperation.class);

    private ProjectService projectService;





    public StreamisProjectUpdateOperation(ProjectService projectService) {
        this.projectService = projectService;
    }


    @Override
    public ProjectResponseRef updateProject(ProjectRequestRef projectRequestRef) throws ExternalOperationFailedException {
        //todo 先不实现了
        return null;
    }

    @Override
    public void setStructureService(StructureService structureService) {

    }
}
