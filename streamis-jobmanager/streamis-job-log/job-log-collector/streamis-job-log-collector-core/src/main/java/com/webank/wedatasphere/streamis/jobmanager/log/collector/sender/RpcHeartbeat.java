package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender;

import java.io.IOException;
import com.google.gson.Gson;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.RpcAuthConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.StreamisLogAppenderConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.linkis.common.utils.Utils;
import org.apache.linkis.server.BDPJettyServerHelper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RpcHeartbeat {

    protected StreamisLogAppenderConfig logAppenderConfig;
    protected Gson gson = BDPJettyServerHelper.gson();

    @PostConstruct
    public void startHeartbeat() {
        String applicationName = logAppenderConfig.getApplicationName();
        String originalString = "streamisRegister";
        String encodedString = Base64.getEncoder().encodeToString(originalString.getBytes());
        HashMap<String,String> registerMap =new HashMap<>();
        registerMap.put("register",encodedString);
        registerMap.put("applicationName",applicationName);
        String registerData = gson.toJson(registerMap);
        try {
            StringEntity params = new StringEntity(registerData);
            httpPost(params);
        } catch (Exception e) {

        }
        int interval = logAppenderConfig.getSenderConfig().getHeartbeatConfig().getHeartbeatInterval();
        HashMap<String,String> heartbeatMap =new HashMap<>();
        registerMap.put("heartbeat","heartbeatData");
        registerMap.put("applicationName",applicationName);
        String heartbeatData = gson.toJson(heartbeatMap);
        Utils.defaultScheduler().scheduleAtFixedRate(() -> {
            try {
                StringEntity params = new StringEntity(heartbeatData);
                httpPost(params);
            } catch (Exception e) {

            }
        }, 0L, interval, TimeUnit.MINUTES);
    }

    private void httpPost(StringEntity params) throws IOException {
        String address = logAppenderConfig.getSenderConfig().getHeartbeatConfig().getHeartbeatAddress();
        RpcAuthConfig authConfig = logAppenderConfig.getSenderConfig().getAuthConfig();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(address);
        httpPost.setEntity(params);
        httpPost.setHeader(authConfig.getTokenUserKey(), authConfig.getTokenUser());
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            int status = response.getStatusLine().getStatusCode();
            if (status >= 200 && status < 300) {
                // 发送心跳成功
                String responseBody = EntityUtils.toString(response.getEntity());
                // 处理响应体
            } else {
                throw new IOException("Failed to send heartbeat");
            }
        } catch (IOException e) {
            throw new IOException("Failed to send heartbeat");
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpClient.close();
            } catch (IOException e) {
                throw new IOException("Failed to close I/O");
            }
        }
    }

}


