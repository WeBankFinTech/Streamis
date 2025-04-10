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

package com.webank.wedatasphere.streamis.jobmanager.manager.dao;

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.*;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.QueryJobListVo;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.VersionDetailVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface StreamJobMapper {

    List<QueryJobListVo> getJobLists(@Param("projectName") String projectName, @Param("userName") String userName, @Param("name") String name,
                                     @Param("status") Integer status, @Param("createBy") String createBy, @Param("label") String label, @Param("manageModeKey") String manageModeKey,
                                     @Param("jobNameList") List<String> jobNameList, @Param("enable") Boolean enable, @Param("jobType")String jobType);

    List<VersionDetailVo> getJobVersionDetails(@Param("jobId") Long jobId);

    StreamJob getJobById(@Param("jobId") Long jobId);

    List<StreamJob> getJobByName(@Param("jobName") String jobName);

    StreamJob getJobByProjectAndJobName(@Param("applicationName") String applicationName);

    List<StreamJobVersion> getJobVersions(@Param("jobId") Long jobId);

    /**
     * Get the latest job version
     * @param jobId job id
     * @return job version
     */
    StreamJobVersion getLatestJobVersion(@Param("jobId") Long jobId);

    StreamJobVersion getJobVersionById(@Param("jobId") Long jobId, @Param("version") String version);

    void insertJob(StreamJob streamJob);

    void insertJobVersion(StreamJobVersion streamJobVersion);

    void updateJob(StreamJob streamJob);

    void updateJobContent(StreamJobVersion streamJobVersion);

    void updateSource(StreamJobVersion streamJobVersion);

    List<StreamJob> getJobListsByProjectName(String projectName);

    VersionDetailVo getVersionDetail(@Param("jobId") Long jobId, @Param("version") String version);

    void insertJobVersionFiles(StreamJobVersionFiles jobVersionFiles);

    List<StreamJobVersionFiles> getStreamJobVersionFiles(@Param("jobId") Long jobId, @Param("jobVersionId") Long jobVersionId);

    StreamJob getCurrentJob(@Param("projectName")String projectName, @Param("jobName")String jobName);

    /**
     * Query and lock current job
     * @param projectName project name
     * @param jobName job name
     * @return stream job
     */
    StreamJob queryAndLockJobInCondition(@Param("projectName")String projectName, @Param("jobName")String jobName);

    /**
     * Query and lock by job id
     * @param jobId job id
     * @return stream job
     */
    StreamJob queryJobById(@Param("jobId")Long jobId);

    void updateJobEnable(StreamJob streamJob);

    StreamJobVersionFiles getJobFileById(@Param("id") Long jobVersionFileId);

    List<StreamJob> batchGetJobByJobIdList(@Param("idList") List<Long> idList);
}
