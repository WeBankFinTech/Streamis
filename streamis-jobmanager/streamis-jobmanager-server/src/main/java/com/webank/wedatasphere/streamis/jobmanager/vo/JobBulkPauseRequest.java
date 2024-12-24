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

package com.webank.wedatasphere.streamis.jobmanager.vo;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Bulk pausing
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JobBulkPauseRequest extends JobBulkRequest{
    /**
     * Is snapshot
     */
    private boolean snapshot = false;

    private boolean skipHookError = false;

    public JobBulkPauseRequest(){
        super();
    }
    public boolean isSnapshot() {
        return snapshot;
    }

    public void setSnapshot(boolean snapshot) {
        this.snapshot = snapshot;
    }

    public boolean isSkipHookError() {
        return skipHookError;
    }

    public void setSkipHookError(boolean skipHookError) {
        this.skipHookError = skipHookError;
    }
}
