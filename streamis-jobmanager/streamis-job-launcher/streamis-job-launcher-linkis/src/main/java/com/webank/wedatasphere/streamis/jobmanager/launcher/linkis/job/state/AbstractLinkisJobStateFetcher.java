
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
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.FlinkJobStateFetchException;
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.exception.StreamisJobLaunchException;
import org.apache.linkis.httpclient.Client;
import org.apache.linkis.httpclient.config.ClientConfig;
import org.apache.linkis.httpclient.config.ClientConfigBuilder;
import org.apache.linkis.httpclient.dws.DWSHttpClient;
import org.apache.linkis.httpclient.dws.authentication.TokenAuthenticationStrategy;
import org.apache.linkis.httpclient.dws.config.DWSClientConfig;
import org.apache.linkis.httpclient.response.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.job.state.ClientConf.*;

/**
 * Linkis Job state fetcher
 * 1) Init to build http client
 * 2) Invoke the getState method to fetch form /api/rest_j/v1/filesystem/getDirFileTrees, the new JobState info
 * 3) Destroy to close the http client when the system is closed
 * @param <T>
 */

public abstract class AbstractLinkisJobStateFetcher<T extends JobState> implements JobStateFetcher<T> {

    private static final Logger logger = LoggerFactory.getLogger(AbstractLinkisJobStateFetcher.class);

    Client client;

    @Override
    public void init() {
        logger.info("Initialize HttpClient start");
        TokenAuthenticationStrategy authenticationStrategy = new TokenAuthenticationStrategy();
        ClientConfig clientConfig = ClientConfigBuilder.newBuilder().addServerUrl("http://" + GATEWAY_ADDRESS)
                .connectionTimeout(CONNECTION_TIMEOUT).discoveryEnabled(false)
                .loadbalancerEnabled(false).maxConnectionSize(MAX_CONNECTION)
                .retryEnabled(false).readTimeout(READ_TIMEOUT)
                .setAuthenticationStrategy(authenticationStrategy).
                        setAuthTokenKey(TOKEN_KEY).setAuthTokenValue(TOKEN_VALUE).build();
        DWSClientConfig dwsClientConfig = new DWSClientConfig(clientConfig);
        dwsClientConfig.setDWSVersion(DWS_VERSION);
        client = new DWSHttpClient(dwsClientConfig, DWS_CLIENTNAME);
        logger.info("Initialize HttpClient finished ");
    }

    @Override
    public T getState(JobInfo jobInfo) {
        try {
            JobStateGetAction getAction = new JobStateGetAction();
            getAction.setUser(jobInfo.getUser());
            getAction.setParameter("path", PATH_PREFIX+jobInfo.getId());
            Result result = client.execute(getAction);
            logger.info("The return result of getState from linkis is {}",result);
            if(result instanceof JobStateResult){
                JobStateResult r = (JobStateResult) result;
                if(r.getStatus()!= 0) {
                    String errMsg = r.getMessage();
                    logger.error("getState failed, msg is {}", errMsg);
                    throw new FlinkJobStateFetchException(-1, "getState failed" + errMsg,null);
                }
                DirFileTree dirFileTrees = r.getDirFileTrees();
                if(dirFileTrees == null){
                    logger.warn("dirFileTrees in the results is null");
                    throw new FlinkJobStateFetchException(-1, "dirFileTrees in the results is null",null);
                }
                JobStateFileInfo fileInfo = getJobStateFileInfo(dirFileTrees);
                return getState(fileInfo);
            }else if(result != null){
                logger.warn("result is not a correct type, result type is {}",
                        result.getClass().getSimpleName());
                throw new FlinkJobStateFetchException(-1, "result is not a correct type",null);
            } else {
                logger.warn("result is null");
                throw new FlinkJobStateFetchException(-1, "result is null",null);
            }
        } catch (FlinkJobStateFetchException e) {
            e.printStackTrace();
            throw new StreamisJobLaunchException.Runtime(e.getErrCode(),e.getMessage(),e.getCause());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new StreamisJobLaunchException.Runtime(-1,e.getMessage(),e.getCause());
        }
    }

    protected abstract boolean isMatch(String path);

    protected abstract T getState(JobStateFileInfo pathInfo);

    private JobStateFileInfo getJobStateFileInfo(DirFileTree dirFileTrees) throws FlinkJobStateFetchException {
        List<DirFileTree> childrenList = dirFileTrees.getChildren();
        DirFileTree lastChildren = new DirFileTree();
        String initModifytime = "0";
        lastChildren.getProperties().put("modifytime", initModifytime);
        for (DirFileTree children:childrenList) {
            String childrenPath = children.getPath();
            if(!isMatch(childrenPath)) continue;
            HashMap<String, String> properties = children.getProperties();
            Boolean isLeaf = children.getIsLeaf();
            if(isLeaf){
                long modifytime = Long.parseLong(properties.get("modifytime"));
                if(modifytime > Long.parseLong(lastChildren.getProperties().get("modifytime"))){
                    lastChildren = children;
                }
            }
        }
        if(initModifytime.equals(lastChildren.getProperties().get("modifytime"))){
            logger.warn("childrenDirFileTrees have no leaf nodes, the obtained checkpoint is null");
            throw new FlinkJobStateFetchException(-1, "childrenDirFileTrees have no leaf nodes, the obtained checkpoint is null",null);
        }
        return new JobStateFileInfo(lastChildren.getName(), lastChildren.getPath(), lastChildren.getParentPath(),
                Long.parseLong(lastChildren.getProperties().get("size")), Long.parseLong(lastChildren.getProperties().get("modifytime")));
    }

    @Override
    public void destroy() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
