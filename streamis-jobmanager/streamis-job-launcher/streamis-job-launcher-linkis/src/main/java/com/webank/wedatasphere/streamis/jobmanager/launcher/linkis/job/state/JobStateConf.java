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

import org.apache.linkis.common.conf.CommonVars;

/**
 * JobState configuration
 */
public class JobStateConf {

    private JobStateConf() {}

    public static final CommonVars<String> CHECKPOINT_PATH_PATTERN = CommonVars.apply("wds.streamis.job.state.checkpoint.path-pattern", "^[\\s\\S]+?/\\w+?/chk-\\d+/_metadata$");

    public static final CommonVars<String> SAVEPOINT_PATH_PATTERN = CommonVars.apply("wds.streamis.job.state.savepoint.path-pattern", "^[\\s\\S]+?/savepoint-[\\w-]+/_metadata$");
}
