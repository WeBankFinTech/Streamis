package com.webank.wedatasphere.streamis.jobmanager.log.server.storage.loadbalancer;

import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket.JobLogBucketConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.context.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Round-robin load balancer
 */
public class RoundRobinLoadBalancer implements JobLogStorageLoadBalancer, JobLogStorageContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(RoundRobinLoadBalancer.class);

    /**
     * Candidate array
     */
    private StorageContextInfo[] candidates = new StorageContextInfo[0];

    /**
     * Lock for candidate array
     */
    private final ReentrantLock candidateLock = new ReentrantLock();
    @Override
    public void onContextEvent(ContextEvent event) {
        if (event instanceof ContextLaunchEvent){
            onLaunchContexts(((ContextLaunchEvent) event).getContextList());
        } else if (event instanceof ContextDownEvent){
            onDownContext(((ContextDownEvent) event).getContextId());
        } else if (event instanceof ContextRefreshAllEvent){
            onRefreshAllContext();
        }
    }

    @Override
    public int priority() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void init() {

    }

    @Override
    public JobLogStorageContext chooseContext(String bucketName, JobLogBucketConfig config) {
        updateCandidateContextWeight();
        candidateLock.lock();
        try {
            int index = selectContext(candidates);
            if (index >= 0){
                StorageContextInfo info = this.candidates[index];
                info.cwt = info.cwt -1;
                LOG.info("Round-Robin chosen context: {} for bucket: {}", info.context.getStorePath(), bucketName);
                return info.context;
            }
        }finally {
            candidateLock.unlock();
        }
        return null;
    }

    private static class StorageContextInfo{

        /**
         * Storage context
         */
        final JobLogStorageContext context;

        /**
         * If the context is working
         */
        boolean online = true;

        /**
         * Weight value
         */
        int wt;

        /**
         * Dynamic weight
         */
        int cwt;

        public StorageContextInfo(JobLogStorageContext context){
            this.context = context;
            this.wt = (int)Math.floor(context.getStoreWeight());
            this.cwt = wt;
        }

        public void refreshWeight(){
            this.wt = (int)Math.floor(context.getStoreWeight());
            if (this.cwt > this.wt){
                this.cwt = this.wt;
            }
        }
    }

    /**
     * Select context
     * @param infoArray info array
     * @return index
     */
    private int selectContext(StorageContextInfo[] infoArray){
        int u = 0;
        int reset = -1;
        while (true){
            for (int i = 0; i < infoArray.length; i ++){
                if (!infoArray[i].online || infoArray[i].cwt <= 0){
                    continue;
                }
                u = i;
                while (i < infoArray.length - 1){
                    i ++;
                    if (!infoArray[i].online || infoArray[i].cwt <= 0){
                        continue;
                    }
                    if ((infoArray[u].wt * 1000 / infoArray[i].wt <
                            infoArray[u].cwt * 1000 / infoArray[i].cwt)){
                        return u;
                    }
                    u = i;
                }
                return u;
            }
            if (++reset > 0){
                return -1;
            }
            for (StorageContextInfo info : infoArray){
                info.cwt = info.wt;
            }
        }

    }
    /**
     * Enlarge the candidate array of context info
     * @param contexts context list
     */
    private void onLaunchContexts(List<JobLogStorageContext> contexts){
        if (contexts.size() > 0){
            candidateLock.lock();
            try{
                StorageContextInfo[] source = candidates;
                int newSize = source.length + contexts.size();
                StorageContextInfo[] dest = new StorageContextInfo[newSize];
                System.arraycopy(source, 0, dest, 0, source.length);
                int offset = source.length;
                for(JobLogStorageContext context : contexts){
                    dest[offset++] = new StorageContextInfo(context);
                }
                this.candidates = dest;
            }finally {
                candidateLock.unlock();
            }
        }
    }

    /**
     * Mark the context has been downed
     * @param contextId context id
     */
    private void onDownContext(String contextId){
        // Need to lock the array ?
        candidateLock.lock();
        try{
            for (StorageContextInfo info : candidates) {
                if (contextId.equals(info.context.getId())) {
                    info.online = false;
                    return;
                }
            }
        } finally {
            candidateLock.unlock();
        }
    }

    /**
     * Refresh all the context
     */
    private void onRefreshAllContext(){
        candidateLock.lock();
        try{
            // Update the dynamic weight
            for (StorageContextInfo info : candidates) {
                info.refreshWeight();
            }
        } finally {
            candidateLock.unlock();
        }
    }
    private void updateCandidateContextWeight(){
        // Empty method
    }

}
