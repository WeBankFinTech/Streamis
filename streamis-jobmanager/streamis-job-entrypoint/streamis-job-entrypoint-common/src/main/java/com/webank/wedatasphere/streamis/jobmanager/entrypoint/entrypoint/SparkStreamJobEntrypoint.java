package com.webank.wedatasphere.streamis.jobmanager.entrypoint.entrypoint;

import com.webank.wedatasphere.streamis.jobmanager.entrypoint.config.SparkStreamJobConfig;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.config.StreamJobConfig;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.exception.JobHeartbeatException;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.message.JobHeartbeatMessage;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.producer.SparkStreamJobHeartbeatProducer;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.producer.StreamJobHeartbeatProducer;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.sender.SparkStreamJobHeartbeatSender;
import com.webank.wedatasphere.streamis.jobmanager.entrypoint.service.StreamJobHeartbeatService;

public class SparkStreamJobEntrypoint extends StreamJobEntrypoint {

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
        StreamJobHeartbeatProducer producer = new SparkStreamJobHeartbeatProducer();
        JobHeartbeatMessage message = producer.produce(config);

        // Create sender
        SparkStreamJobHeartbeatSender sender = new SparkStreamJobHeartbeatSender();

        // Send job heartbeat
        StreamJobHeartbeatService service = new StreamJobHeartbeatService();
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
