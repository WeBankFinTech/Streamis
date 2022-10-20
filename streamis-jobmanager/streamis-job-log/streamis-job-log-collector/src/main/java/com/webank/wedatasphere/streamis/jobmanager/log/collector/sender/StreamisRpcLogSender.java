package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender;


import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.RpcLogSenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.buf.SendBuffer;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.http.AbstractHttpLogSender;
import com.webank.wedatasphere.streamis.jobmanager.log.entities.StreamisLogEvent;
import com.webank.wedatasphere.streamis.jobmanager.log.entities.StreamisLogEvents;

/**
 * Log sender for streamis
 */
public class StreamisRpcLogSender extends AbstractHttpLogSender<StreamisLogEvent, StreamisLogEvents> {

    /**
     * Each sender register an application
     */
    private final String applicationName;

    public StreamisRpcLogSender(String applicationName, RpcLogSenderConfig rpcSenderConfig) {
        super(rpcSenderConfig);
        this.applicationName = applicationName;
    }

    /**
     * Aggregate to streamis log events
     * @param sendBuffer send buffer
     * @return
     */
    @Override
    protected StreamisLogEvents aggregateBuffer(SendBuffer<StreamisLogEvent> sendBuffer) {
        int remain = sendBuffer.remaining();
        StreamisLogEvent[] logEvents = new StreamisLogEvent[remain];
        sendBuffer.readBuf(logEvents, 0, logEvents.length);
        return new StreamisLogEvents(applicationName, logEvents);
    }
}
