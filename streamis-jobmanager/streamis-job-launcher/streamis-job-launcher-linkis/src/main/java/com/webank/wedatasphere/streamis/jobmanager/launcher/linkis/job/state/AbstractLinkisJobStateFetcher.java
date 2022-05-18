
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
        logger.info("HttpClient init() finished ");
    }

    @Override
    public T getState(JobInfo jobInfo) {
        JobStateGetAction getAction = new JobStateGetAction();
        getAction.setUser(jobInfo.getUser());
        getAction.setParameter("path", PATH_PREFIX+jobInfo.getId());
        Result result = client.execute(getAction);
        Checkpoint checkpoint;
        if(result instanceof JobStateResult){
            JobStateResult r = (JobStateResult) result;
            if(r.getStatus()!= 0) throw new IllegalArgumentException(r.getMessage());
            return getState(r);
        }
        logger.warn("getState() return result: {} is not JobStateResult type",result);
        return null;
    }

    protected abstract T getState(JobStateResult jobStateResult);

    @Override
    public void destroy() {
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
