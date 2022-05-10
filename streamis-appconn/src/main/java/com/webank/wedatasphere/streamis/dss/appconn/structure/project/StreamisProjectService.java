package com.webank.wedatasphere.streamis.dss.appconn.structure.project;

import com.webank.wedatasphere.dss.standard.app.structure.project.*;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.DSSProjectContentRequestRef;
import com.webank.wedatasphere.streamis.dss.appconn.structure.ref.StreamisProjectContentReqRef;
import com.webank.wedatasphere.streamis.dss.appconn.structure.ref.StreamisProjectUpdateReqRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Streamis project service
 */
public class StreamisProjectService extends ProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(StreamisProjectService.class);
    @Override
    protected ProjectCreationOperation<DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl> createProjectCreationOperation() {
        //TODO create project create operation
        return null;
    }

    @Override
    protected ProjectUpdateOperation<StreamisProjectUpdateReqRef> createProjectUpdateOperation() {
        //TODO create project update operation
        return null;
    }

    @Override
    protected ProjectDeletionOperation<?> createProjectDeletionOperation() {
        // Not need to delete the project
        return null;
    }

    @Override
    protected ProjectSearchOperation<StreamisProjectContentReqRef> createProjectSearchOperation() {
        //TODO create search operation
        return null;
    }
}
