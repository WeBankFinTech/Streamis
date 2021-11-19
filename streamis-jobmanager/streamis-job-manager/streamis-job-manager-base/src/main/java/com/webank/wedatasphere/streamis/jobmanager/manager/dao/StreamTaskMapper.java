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

import com.webank.wedatasphere.streamis.jobmanager.manager.entity.StreamTask;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface StreamTaskMapper {

    void insertTask(StreamTask streamTask);

    void updateTask(StreamTask streamTask);

    List<StreamTask> getByJobVersionId(@Param("jobVersionId") Long jobVersionId, @Param("version") String version);

    StreamTask getRunningTaskByJobId(@Param("jobId") Long jobId);

    StreamTask getTaskById(@Param("id") Long id);

    List<StreamTask> getTasksByJobIdAndJobVersionId(@Param("jobId") Long jobId, @Param("jobVersionId") Long jobVersionId);

    List<StreamTask> getTasksByStatus(List<Integer> status);

    String getTask(@Param("jobId") Long jobId, @Param("version") String version);
}
