package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender;

import java.io.IOException;
import com.google.gson.Gson;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.RpcAuthConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.StreamisLogAppenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.entities.StreamisHeartbeat;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
public class RpcHeartbeatService {

    protected StreamisLogAppenderConfig logAppenderConfig;

    private final Gson gson = new Gson();

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @PostConstruct
    public void startHeartbeat() {
        String applicationName = logAppenderConfig.getApplicationName();
        String originalString = "streamisRegister";
        String encodedString = Base64.getEncoder().encodeToString(originalString.getBytes());
        StreamisHeartbeat streamisHeartbeat = new StreamisHeartbeat();
        streamisHeartbeat.setApplicationName(applicationName);
        streamisHeartbeat.setPasswordOrHeartbeat(encodedString);
        streamisHeartbeat.setSign("register");
        String registerData = gson.toJson(streamisHeartbeat);
        try {
            StringEntity params = new StringEntity(registerData);
            httpPostWithRetry(params);
        } catch (Exception e) {
            System.err.println("flink 应用请求注册失败，appName:" + applicationName);
        }
        int interval = logAppenderConfig.getSenderConfig().getHeartbeatConfig().getHeartbeatInterval();
        streamisHeartbeat.setPasswordOrHeartbeat("heartbeatData");
        streamisHeartbeat.setSign("heartbeat");
        String heartbeatData = gson.toJson(streamisHeartbeat);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                StringEntity params = new StringEntity(heartbeatData);
                httpPostWithRetry(params);
            } catch (Exception e) {
                System.err.println("flink 应用请求心跳失败，appName:" + applicationName);
            }
        }, 5L * 60 * 1000, interval, TimeUnit.MILLISECONDS);
    }

    private void httpPostWithRetry(StringEntity params) {
        int retryCount = 0;
        while (retryCount < 3) {
            try {
                httpPost(params);
                return; // 如果执行成功则直接返回
            } catch (Exception e) {
                retryCount++;
                if (retryCount < 3) {
                    System.err.println("httpPost请求失败，重试第 " + retryCount + " 次");
                    try {
                        Thread.sleep(5000); // 间隔5秒后重试
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        System.err.println("httpPost请求重试次数达到上限，请求失败");
    }

    private void httpPost(StringEntity params) throws IOException {
        String address = logAppenderConfig.getSenderConfig().getHeartbeatConfig().getHeartbeatAddress();
        RpcAuthConfig authConfig = logAppenderConfig.getSenderConfig().getAuthConfig();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(address);
        httpPost.setEntity(params);
        httpPost.setHeader(authConfig.getTokenUserKey(), authConfig.getTokenUser());
        String tokenValue = logAppenderConfig.getSenderConfig().getAuthConfig().getTokenCode();
        if (null != tokenValue && !tokenValue.trim().equals("")){
            httpPost.setHeader(logAppenderConfig.getSenderConfig().getAuthConfig().getTokenCodeKey(), tokenValue);
        }
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                // 发送心跳成功
                String responseBody = EntityUtils.toString(response.getEntity());
                // 处理响应体
            } else {
                System.err.println("Failed to send heartbeat");
            }
        } catch (IOException e) {
            System.err.println("Failed to send heartbeat");
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                System.err.println("Failed to close I/O");
            }
        }
    }

}


