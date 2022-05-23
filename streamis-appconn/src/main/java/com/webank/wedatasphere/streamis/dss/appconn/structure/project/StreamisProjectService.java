package com.webank.wedatasphere.streamis.dss.appconn.structure.project;

import com.webank.wedatasphere.dss.standard.app.structure.project.*;
import com.webank.wedatasphere.dss.standard.app.structure.project.ref.DSSProjectContentRequestRef;
import com.webank.wedatasphere.streamis.dss.appconn.structure.ref.StreamisProjectContentReqRef;
import com.webank.wedatasphere.streamis.dss.appconn.structure.ref.StreamisProjectUpdateReqRef;

/**
 * Streamis project service
 */
public class StreamisProjectService extends ProjectService {

    // TODO use the StreamisProjectContentReqRef as parameter type
    @Override
    protected ProjectCreationOperation<DSSProjectContentRequestRef.DSSProjectContentRequestRefImpl> createProjectCreationOperation() {
        return new StreamisProjectCreationOperation();
    }

    // TODO use the StreamisProjectUpdateReqRef as parameter type
    @Override
    protected ProjectUpdateOperation<StreamisProjectUpdateReqRef> createProjectUpdateOperation() {
        return new StreamisProjectUpdateOperation();
    }


    // TODO deletion operation
    @Override
    protected ProjectDeletionOperation<StreamisProjectContentReqRef> createProjectDeletionOperation() {
        return new StreamisPrejectDeleteOperation();
    }

    // TODO query operation
    @Override
    protected ProjectSearchOperation<StreamisProjectContentReqRef> createProjectSearchOperation() {
        return new StreamisProjectSearchOperation();
    }
}
