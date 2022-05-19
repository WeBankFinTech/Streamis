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

package com.webank.wedatasphere.streamis.jobmanager.manager.scheduler.events;

import com.webank.wedatasphere.streamis.jobmanager.manager.scheduler.StreamisSchedulerEvent;
import com.webank.wedatasphere.streamis.jobmanager.manager.scheduler.exception.StreamisScheduleException;
import com.webank.wedatasphere.streamis.jobmanager.manager.scheduler.exception.StreamisScheduleRetryException;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.scheduler.executer.ErrorExecuteResponse;
import org.apache.linkis.scheduler.executer.ExecuteRequest;
import org.apache.linkis.scheduler.listener.JobListener;
import org.apache.linkis.scheduler.queue.Job;
import org.apache.linkis.scheduler.queue.JobInfo;
import org.apache.linkis.scheduler.queue.SchedulerEvent;
import org.apache.linkis.scheduler.queue.SchedulerEventState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract implement
 */
public abstract class AbstractStreamisSchedulerEvent extends Job implements StreamisSchedulerEvent {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractStreamisSchedulerEvent.class);

    public static final int MAX_RETRY_NUM = 3;

    private CompletableFuture<SchedulerEvent> completeFuture;

    private int maxRetryNum = MAX_RETRY_NUM;

    private final AtomicBoolean initialized = new AtomicBoolean(false);
    /**
     * Extra message (output)
     */
    private String extraMessage;

    /**
     * Use schedule id instead of queue id
     */
    protected String scheduleId;

    /**
     * Tenancy name
     */
    private String tenancy;

    public AbstractStreamisSchedulerEvent(){
        setJobListener(new JobListener() {
            @Override
            public void onJobInited(Job job) {
                // Ignore init
                if (!initialized.get()){
                    try {
                        prepare(getJobInfo());
                    } catch (StreamisScheduleException e) {
                        // Convert to runtime exception
                        throw new StreamisScheduleException.Runtime(e.getMessage(), e.getCause());
                    }
                }
            }

            @Override
            public void onJobRunning(Job job) {
                // Ignore job running
            }

            @Override
            public void onJobScheduled(Job job) {
                // Ignore job scheduled
            }

            @Override
            public void onJobCompleted(Job job) {
                JobInfo jobInfo = getJobInfo();
                if (getState() == SchedulerEventState.Failed()){
                    ErrorExecuteResponse response = getErrorResponse();
                    try {
                        Throwable t = null;
                        if (Objects.nonNull(response)){
                            extraMessage = response.message();
                            t = response.t();
                        }
                        errorHandle(jobInfo, t);
                    }catch(Exception e){
                        LOG.warn("Unable to process the error handler for scheduler event: [{}]", getName(), e);
                        // Ignore
                    }
                }
                if (Objects.nonNull(completeFuture)){
                    completeFuture.complete(job);
                }
                try {
                    postHandle(jobInfo);
                } catch (StreamisScheduleException e) {
                    // Convert to runtime exception
                    throw new StreamisScheduleException.Runtime(e.getMessage(), e.getCause());
                }
            }

            @Override
            public void onJobWaitForRetry(Job job) {
                // Ignore job wait for retry
                extraMessage = "Wait for the scheduler retry to schedule";
            }
        });
    }

    @Override
    public String getId() {
        if (StringUtils.isNotBlank(this.scheduleId)){
            return scheduleId;
        }
        return super.getId();
    }

    @Override
    public void init() throws Exception {
        prepare(getJobInfo());
    }

    @Override
    public ExecuteRequest jobToExecuteRequest() throws Exception {
        return new LocalExecuteRequest();
    }

    @Override
    public String getName() {
        return "Streamis-schedule-event-" + getId();
    }

    @Override
    public JobInfo getJobInfo() {
        return new JobInfo(getId(), this.extraMessage, this.getState().toString(), this.getProgress(), null);
    }

    @Override
    public void setCompleteFuture(CompletableFuture<SchedulerEvent> completeFuture) {
        this.completeFuture = completeFuture;
    }

    @Override
    public void setTenancy(String tenancy) {
        this.tenancy = tenancy;
    }

    @Override
    public String getTenancy() {
        return tenancy;
    }

    @Override
    public int getMaxRetryNum() {
        return maxRetryNum;
    }

    public void setMaxRetryNum(int maxRetryNum){
        this.maxRetryNum = maxRetryNum;
    }

    @Override
    public synchronized void prepare(JobInfo jobInfo) throws StreamisScheduleException{
        if (!initialized.get()){
            prepareHandle(jobInfo);
            initialized.set(true);
        }
    }

    @Override
    public void close() throws IOException {

    }

    /**
     * Prepare handle
     * @param jobInfo job info
     */
    protected abstract void prepareHandle(JobInfo jobInfo) throws StreamisScheduleException;

    /**
     * Request to execute in local thread
     */
    public class LocalExecuteRequest implements ExecuteRequest{

        @Override
        public String code() {
            return null;
        }

        public void localExecute() throws StreamisScheduleException, StreamisScheduleRetryException{
            try{
                schedule(getJobInfo());
            } catch (StreamisScheduleRetryException e){
                if (e.getRetryNum() > 0){
                    setMaxRetryNum(e.getRetryNum());
                }
                throw e;
            }
        }
    }
}
