package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.http;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.AbstractRpcLogSender;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.RpcSenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.SendLogExceptionStrategy;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.buf.SendBuffer;
import com.webank.wedatasphere.streamis.jobmanager.log.entities.LogElement;
import org.apache.http.client.HttpClient;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractHttpLogSender<T extends LogElement, E> extends AbstractRpcLogSender<T, E> {

    /**
     * Retry strategy
     */
    private SendLogExceptionStrategy<T> sendRetryStrategy;

    /**
     * Exception counter
     */
    private AtomicInteger exceptionCounter = new AtomicInteger();
    /**
     * Hold the global http client
     */
    private HttpClient globalHttpClient;

    public AbstractHttpLogSender(RpcSenderConfig rpcSenderConfig, int cacheSize, int sendBufSize) {
        super(rpcSenderConfig, cacheSize, sendBufSize);
        this.globalHttpClient = HttpClientTool.createHttpClient(rpcSenderConfig);
        this.sendRetryStrategy = new SendLogExceptionStrategy<T>(this) {
            @Override
            public int retryCount() {
                return rpcSenderConfig.getSendRetryCnt();
            }

            @Override
            public SendLogExceptionStrategy.RetryDescription onException(Exception e, SendBuffer<T> sendBuffer) {

                return null;
            }
        };
    }

    @Override
    protected void doSend(E aggregatedEntity, RpcSenderConfig rpcSenderConfig) {
        if (aggregatedEntity instanceof LogElement){
            long timestamp = ((LogElement) aggregatedEntity).getLogTimeStamp();
            if (System.currentTimeMillis() - timestamp > rpcSenderConfig.getMaxDelayTimeInSec() * 1000L){
                // Abort the entity
                return;
            }
        }

    }
}
