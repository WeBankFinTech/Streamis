package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state;

import org.apache.linkis.httpclient.dws.annotation.DWSHttpMessageResult;
import org.slf4j.Logger;

@DWSHttpMessageResult("/api/rest_j/v\\d+/filesystem/getDirFileTrees")
public class JobStateResult extends AbstractJobStateResult {

    private DirFileTree dirFileTrees;

    public DirFileTree getDirFileTrees() {
        return dirFileTrees;
    }

    public void setDirFileTrees(DirFileTree dirFileTrees) {
        this.dirFileTrees = dirFileTrees;
    }

    @Override
    public Logger logger() {
        return null;
    }

}
