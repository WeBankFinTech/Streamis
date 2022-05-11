package com.webank.wedatasphere.streamis.dss.appconn.structure.project;

import com.webank.wedatasphere.dss.standard.app.structure.project.*;
import com.webank.wedatasphere.streamis.dss.appconn.structure.ref.StreamisProjectContentReqRef;

/**
 * Streamis project service
 */
public class StreamisProjectService extends ProjectService {

    @Override
    protected ProjectCreationOperation createProjectCreationOperation() {
        return new StreamisProjectCreationOperation();
    }

    @Override
    protected ProjectUpdateOperation createProjectUpdateOperation() {
        return new StreamisProjectUpdateOperation();
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
