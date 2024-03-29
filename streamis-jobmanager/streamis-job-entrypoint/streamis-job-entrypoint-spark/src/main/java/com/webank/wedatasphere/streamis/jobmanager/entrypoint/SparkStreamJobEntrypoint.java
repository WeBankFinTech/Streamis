package com.webank.wedatasphere.streamis.jobmanager.entrypoint;

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.config.SparkStreamJobConfig;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.config.StreamJobConfig;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.exception.JobHeartbeatException;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.message.JobHeartbeatMessage;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.producer.StreamJobHeartbeatProducer;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.sender.SparkStreamJobHeartbeatSender;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.service.StreamJobHeartbeatService;
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.utils.GsonUtil;
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.utils.HttpClientUtil;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class SparkStreamJobEntrypoint extends StreamJobEntrypoint {

    /**
     * @param config
     * @return
     */
    @Override
    public void register(StreamJobConfig config, StreamJobHeartbeatProducer producer) throws JobHeartbeatException {
        if (!this.checkConfig(config)) {
            throw new JobHeartbeatException(-1, "Incorrect configuration parameters");
        }

        // Produce message
        JobHeartbeatMessage message = producer.produce(config);

        // Create sender and init
        SparkStreamJobHeartbeatSender sender = new SparkStreamJobHeartbeatSender();

        Properties prop = HttpClientUtil.getSecurityProperties();
        CloseableHttpClient httpClient = HttpClientUtil.createHttpClientUtil(prop);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("message", message);
        StringEntity entity = null;
        try {
            entity = new StringEntity(GsonUtil.toJson(requestBody));
        } catch (UnsupportedEncodingException e) {
            throw new UnsupportedEncodingException(e);
        }
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        HttpPost postRequest = HttpClientUtil.getPostRequest(config.getStreamisServerUrl(), entity);

        sender.init(httpClient, postRequest);

        // Send job heartbeat
        StreamJobHeartbeatService service = new StreamJobHeartbeatService();
        service.init();
        service.start(message, sender);

    }

    @Override
    public Boolean checkConfig(StreamJobConfig config) {
        if (config instanceof SparkStreamJobConfig) {
            return super.checkConfig(config);
        }
        return false;
    }
}
