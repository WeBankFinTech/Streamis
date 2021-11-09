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
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.QueryJobListVO;
import com.webank.wedatasphere.streamis.jobmanager.manager.entity.vo.VersionDetailVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface StreamJobMapper {

    List<QueryJobListVO> getJobLists(@Param("projectName") String projectName, @Param("name") String name,
        @Param("status") Integer status, @Param("createBy") String createBy);

    StreamJob getJobById(@Param("jobId") Long jobId);


    List<StreamJobVersion> getJobVersions(@Param("jobId") Long jobId);

    StreamJobVersion getJobVersionById(@Param("jobId") Long jobId, @Param("version") String version);

    void insertJob(StreamJob streamJob);

    void insertJobVersion(StreamJobVersion streamJobVersion);

    void updateJob(StreamJob streamJob);

    List<StreamJob> getJobListsByProjectName(String projectName);

    VersionDetailVO getVersionDetail(@Param("jobId") Long jobId, @Param("version") String version);

    void insertJobVersionFiles(StreamJobVersionFiles jobVersionFiles);

    List<StreamJobVersionFiles> getStreamJobVersionFiles(@Param("jobId") Long jobId, @Param("jobVersionId") Long jobVersionId);

    StreamJob getCurrentJob(@Param("projectName")String projectName, @Param("jobName")String jobName);
}
