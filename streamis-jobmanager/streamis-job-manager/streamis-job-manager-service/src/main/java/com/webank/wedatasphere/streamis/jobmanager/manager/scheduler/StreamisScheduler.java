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

package com.webank.wedatasphere.streamis.jobmanager.manager.scheduler;

import com.webank.wedatasphere.streamis.jobmanager.manager.scheduler.exception.StreamisScheduleException;
import org.apache.linkis.scheduler.AbstractScheduler;
import org.apache.linkis.scheduler.SchedulerContext;
import org.apache.linkis.scheduler.queue.Job;
import org.apache.linkis.scheduler.queue.JobInfo;
import org.apache.linkis.scheduler.queue.SchedulerEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.function.Function;

/**
 * Generic scheduler for streamis
 */
public class StreamisScheduler extends AbstractScheduler implements FutureScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(StreamisScheduler.class);

    public StreamisScheduler(){

    }
    @Override
    public String getName() {
        return "Streamis-Tenancy-Scheduler";
    }

    @Override
    public SchedulerContext getSchedulerContext() {
        return null;
    }

    @Override
    public void submit(SchedulerEvent event) {
        super.submit(event);
    }

    @Override
    public <T> Future<T> submit(SchedulerEvent event, Function<SchedulerEvent, T> resultMapping) throws StreamisScheduleException {
        // Empty future
        CompletableFuture<SchedulerEvent> completableFuture = new CompletableFuture<>();
        if (event instanceof StreamisSchedulerEvent){
            StreamisSchedulerEvent schedulerEvent = (StreamisSchedulerEvent)event;
            // Set the completed future
            schedulerEvent.setCompleteFuture(completableFuture);
            // Invoke the prepare method
            JobInfo jobInfo = ((Job)event).getJobInfo();
            try {
                schedulerEvent.prepare(jobInfo);
            }catch(Exception e){
                try {
                    schedulerEvent.errorHandle(jobInfo, e);
                }catch(Exception he){
                    LOG.warn("Unable to process the error handler for scheduler event: [{}]", ((Job) event).getName(), he);
                    // Ignore the exception
                }
                if (e instanceof StreamisScheduleException){
                    throw (StreamisScheduleException) e;
                }
                throw new StreamisScheduleException("Fail to invoke prepare method in scheduler event: [" + ((Job) event).getName() + "]", e);
            }
        } else{
            LOG.warn("Scheduler event type {} is not support future return", event.getClass().getCanonicalName());
            completableFuture.complete(event);
        }
        submit(event);
        return completableFuture.thenApply(resultMapping);
    }
}
