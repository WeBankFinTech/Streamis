package com.webank.wedatasphere.streamis.jobmanager.entrypoint.entrypoint;

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.config.FlinkStreamJobConfig;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.config.StreamJobConfig;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.exception.JobHeartbeatException;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.message.JobHeartbeatMessage;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.producer.FlinkStreamJobHeartbeatProducer;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.producer.StreamJobHeartbeatProducer;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.sender.FlinkStreamJobHeartbeatSender;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.service.StreamJobHeartbeatService;
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.utils.GsonUtil;
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.utils.HttpClientUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class FlinkStreamJobEntrypoint extends StreamJobEntrypoint {

    /**
     * @param config
     * @return
     */
    @Override
    public void register(StreamJobConfig config) throws JobHeartbeatException {
        if (!this.checkConfig(config)) {
            throw new JobHeartbeatException(-1, "Incorrect configuration parameters");
        }

        // Create producer
        StreamJobHeartbeatProducer producer = new FlinkStreamJobHeartbeatProducer();
        JobHeartbeatMessage message = producer.produce(config);

        // Create sender and init
        FlinkStreamJobHeartbeatSender sender = new FlinkStreamJobHeartbeatSender();

        CloseableHttpClient httpClient = HttpClientUtil.createHttpClientUtil("flink");

        //todo uri base on streamis
        String uri = "http://127.0.0.1:9091/streamis/jobheartbeat/send";
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("message", message);
        StringEntity entity = null;
        try {
            entity = new StringEntity(GsonUtil.toJson(requestBody));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        entity.setContentEncoding("UTF-8");
        entity.setContentType("application/json");
        HttpPost postRequest = HttpClientUtil.getPostRequest(uri, entity);

        sender.init(httpClient, postRequest);

        // Send job heartbeat
        StreamJobHeartbeatService service = new StreamJobHeartbeatService();
        service.init();
        service.start(message, sender);

    }

    @Override
    public Boolean checkConfig(StreamJobConfig config) {
        if (config instanceof FlinkStreamJobConfig) {
            return super.checkConfig(config) &&
                    StringUtils.isNoneBlank(((FlinkStreamJobConfig) config).getApplicationUrl()) &&
                    StringUtils.isNoneBlank(((FlinkStreamJobConfig) config).getJobId());
        }
        return false;
    }
}
