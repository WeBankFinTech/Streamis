package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.http;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.RpcLogSenderConfig;
import org.apache.http.Header;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Http clients
 */
public class HttpClientTool {

    /**
     * Connect timeout
     */
    public static final int DEFAULT_CONNECT_TIMEOUT = 3000;

    /**
     * Socket timeout
     */
    public static final int DEFAULT_SOCKET_TIMEOUT = 15000;

    /**
     * Max connections
     */
    public static final int DEFAULT_MAX_CONN = 10;

    /**
     * Create http client
     * @param rpcSenderConfig rpc sender config
     * @return http client
     */
    public static HttpClient createHttpClient(RpcLogSenderConfig rpcSenderConfig){
        int connectTimeout = rpcSenderConfig.getConnectionTimeout() > 0? rpcSenderConfig.getConnectionTimeout() : DEFAULT_CONNECT_TIMEOUT;
        int socketTimeout = rpcSenderConfig.getSocketTimeout() > 0? rpcSenderConfig.getSocketTimeout() : DEFAULT_SOCKET_TIMEOUT;
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setConnectionRequestTimeout(socketTimeout)
                .setSocketTimeout(socketTimeout)
                .build();
        int maxConsumeThread = rpcSenderConfig.getCacheConfig().getMaxConsumeThread();
        int maxConn = maxConsumeThread > 0? maxConsumeThread : DEFAULT_MAX_CONN;
        HttpClientBuilder clientBuilder = HttpClients.custom();
        String tokenValue = rpcSenderConfig.getAuthConfig().getTokenCode();
        List<Header> defaultHeaders = new ArrayList<>();
        if (null != tokenValue && !tokenValue.trim().equals("")){
            defaultHeaders.add(new BasicHeader(rpcSenderConfig.getAuthConfig().getTokenCodeKey(), tokenValue));
        }
        clientBuilder.setDefaultRequestConfig(requestConfig).setDefaultHeaders(defaultHeaders)
                .useSystemProperties().setMaxConnTotal(maxConn).setMaxConnPerRoute(maxConn);
        CloseableHttpClient httpClient = clientBuilder.build();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            httpClient.close();
                        } catch (IOException e) {
                            // Ignore
                        }
                    }
                }));
        return httpClient;
    }
}
