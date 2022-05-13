package com.webank.wedatasphere.streamis.dss.appconn.structure.project;

import com.webank.wedatasphere.dss.standard.app.structure.project.*;
import com.webank.wedatasphere.streamis.dss.appconn.structure.ref.StreamisProjectContentReqRef;

/**
 * Streamis project service
 */
public class StreamisProjectService extends ProjectService {

    // TODO use the StreamisProjectContentReqRef as parameter type
    @Override
    protected ProjectCreationOperation createProjectCreationOperation() {
        return new StreamisProjectCreationOperation();
    }

    // TODO use the StreamisProjectUpdateReqRef as parameter type
    @Override
    protected ProjectUpdateOperation createProjectUpdateOperation() {
        return new StreamisProjectUpdateOperation();
    }


    // TODO deletion operation
    @Override
    protected ProjectDeletionOperation<?> createProjectDeletionOperation() {
        // Not need to delete the project
        return null;
    }

    // TODO query operation
    @Override
    protected ProjectSearchOperation<StreamisProjectContentReqRef> createProjectSearchOperation() {
        //TODO create search operation
        return null;
    }
}
