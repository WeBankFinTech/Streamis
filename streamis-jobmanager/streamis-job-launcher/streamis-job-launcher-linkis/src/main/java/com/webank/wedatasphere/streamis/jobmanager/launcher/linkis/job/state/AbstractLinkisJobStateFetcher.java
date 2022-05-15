package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state;

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.JobInfo;
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobState;
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobStateFetcher;

public class AbstractLinkisJobStateFetcher<T extends JobState> implements JobStateFetcher<T> {
    @Override
    public T getState(JobInfo jobInfo) {
        return null;
    }
}
