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

package com.webank.wedatasphere.streamis.jobmanager.launcher.entity.vo;

import java.util.List;


public class ConfigKeyVO {

    private Long jobId;

    private List<ConfigRelationVO> resourceConfig;

    private List<ConfigRelationVO> produceConfig;

    private List<ConfigRelationVO> parameterConfig;

    private List<ConfigRelationVO> alarmConfig;

    private List<ConfigRelationVO> permissionConfig;


    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public List<ConfigRelationVO> getResourceConfig() {
        return resourceConfig;
    }

    public void setResourceConfig(List<ConfigRelationVO> resourceConfig) {
        this.resourceConfig = resourceConfig;
    }

    public List<ConfigRelationVO> getProduceConfig() {
        return produceConfig;
    }

    public void setProduceConfig(List<ConfigRelationVO> produceConfig) {
        this.produceConfig = produceConfig;
    }

    public List<ConfigRelationVO> getParameterConfig() {
        return parameterConfig;
    }

    public void setParameterConfig(List<ConfigRelationVO> parameterConfig) {
        this.parameterConfig = parameterConfig;
    }

    public List<ConfigRelationVO> getAlarmConfig() {
        return alarmConfig;
    }

    public void setAlarmConfig(List<ConfigRelationVO> alarmConfig) {
        this.alarmConfig = alarmConfig;
    }

    public List<ConfigRelationVO> getPermissionConfig() {
        return permissionConfig;
    }

    public void setPermissionConfig(List<ConfigRelationVO> permissionConfig) {
        this.permissionConfig = permissionConfig;
    }
}
