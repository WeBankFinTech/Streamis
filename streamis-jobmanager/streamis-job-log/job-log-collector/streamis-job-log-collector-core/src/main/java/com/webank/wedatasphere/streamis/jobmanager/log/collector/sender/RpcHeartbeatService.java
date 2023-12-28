package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.RpcAuthConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.StreamisLogAppenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.entities.StreamisHeartbeat;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.Base64;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class RpcHeartbeatService {

    protected StreamisLogAppenderConfig logAppenderConfig;

    private CloseableHttpClient httpClient = HttpClients.createDefault();

    public RpcHeartbeatService(StreamisLogAppenderConfig logAppenderConfig) {
        this.logAppenderConfig = logAppenderConfig;
    }

    private ScheduledExecutorService scheduler;

    private ThreadFactory threadFactory(String threadName, Boolean isDaemon) {
        return new ThreadFactory() {
            AtomicInteger num = new AtomicInteger(0);

            @Override
            public Thread newThread(Runnable r) {
                    Thread t = new Thread(r);
                    t.setDaemon(isDaemon);
                    t.setName(threadName + num.incrementAndGet());
                    return t;
            }
        };
    }

    public void startHeartbeat() {
        System.out.println("Start to heart register.");
        ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(1, threadFactory("Streamis-Log-Default-Scheduler-Thread-", true));
        scheduler.setMaximumPoolSize(1);
        scheduler.setKeepAliveTime(5, TimeUnit.MINUTES);
        this.scheduler = scheduler;

        String applicationName = logAppenderConfig.getApplicationName();
        String originalString = "streamisRegister";
        String encodedString = Base64.getEncoder().encodeToString(originalString.getBytes());
        StreamisHeartbeat streamisHeartbeat = new StreamisHeartbeat();
        streamisHeartbeat.setApplicationName(applicationName);
        streamisHeartbeat.setPasswordOrHeartbeat(encodedString);
        streamisHeartbeat.setSign("register");
        String registerData = streamisHeartbeat.toJson();
        try {
            StringEntity params = new StringEntity(registerData, "UTF-8");
            params.setContentType(ContentType.APPLICATION_JSON.toString());
            httpPostWithRetry(params);
        } catch (Exception e) {
            System.err.println("flink 应用请求注册失败，appName:" + applicationName);
        }
        int interval = logAppenderConfig.getSenderConfig().getHeartbeatInterval();
        streamisHeartbeat.setPasswordOrHeartbeat("heartbeatData");
        streamisHeartbeat.setSign("heartbeat");
        String heartbeatData = streamisHeartbeat.toJson();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                StringEntity params = new StringEntity(heartbeatData, "UTF-8");
                params.setContentType(ContentType.APPLICATION_JSON.toString());
                httpPostWithRetry(params);
            } catch (Exception e) {
                System.err.println("flink 应用请求心跳失败，appName:" + applicationName);
            }
        }, 1L * 10 * 1000, interval, TimeUnit.MILLISECONDS);
        System.out.println("End to heart beat.");
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
        String address = logAppenderConfig.getSenderConfig().getHeartbeatAddress();
        RpcAuthConfig authConfig = logAppenderConfig.getSenderConfig().getAuthConfig();
        HttpPost httpPost = new HttpPost(address);
        httpPost.setEntity(params);
        httpPost.setHeader(authConfig.getTokenUserKey(), authConfig.getTokenUser());
        String tokenValue = logAppenderConfig.getSenderConfig().getAuthConfig().getTokenCode();
        if (null != tokenValue && !tokenValue.trim().equals("")){
            httpPost.setHeader(logAppenderConfig.getSenderConfig().getAuthConfig().getTokenCodeKey(), tokenValue);
        }
        CloseableHttpResponse response = null;
        try {
            System.err.println("Start to send request, status : ");
            response = httpClient.execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            System.err.println("End to send request, status : " + status);
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


