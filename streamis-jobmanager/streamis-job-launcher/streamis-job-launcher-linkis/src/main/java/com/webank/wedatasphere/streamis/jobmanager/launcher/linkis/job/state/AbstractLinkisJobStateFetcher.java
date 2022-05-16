
/*
 * Copyright 2021 WeBank
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state;

import com.webank.wedatasphere.streamis.jobmanager.launcher.job.JobInfo;
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobState;
import com.webank.wedatasphere.streamis.jobmanager.launcher.job.state.JobStateFetcher;

/**
 * Linkis Job state fetcher
 * 1) Init to build http client
 * 2) Invoke the getState method to fetch form /api/rest_j/v1/filesystem/getDirFileTrees, the new JobState info
 * 3) Destroy to close the http client when the system is closed
 * @param <T>
 */
public class AbstractLinkisJobStateFetcher<T extends JobState> implements JobStateFetcher<T> {
    @Override
    public void init() {

    }

    @Override
    public T getState(JobInfo jobInfo) {
        return null;
    }

    @Override
    public void destroy() {

    }
}
