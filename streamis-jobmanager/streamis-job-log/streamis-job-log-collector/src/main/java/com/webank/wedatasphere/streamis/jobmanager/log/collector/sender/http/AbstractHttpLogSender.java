package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.http;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.AbstractRpcLogSender;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.RpcSenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.SendLogExceptionStrategy;
import com.webank.wedatasphere.streamis.jobmanager.log.entities.LogElement;

public abstract class AbstractHttpLogSender<T extends LogElement, E> extends AbstractRpcLogSender<T, E> {

    /**
     * Retry strategy
     */
    private SendLogExceptionStrategy sendRetryStrategy;

    public AbstractHttpLogSender(RpcSenderConfig rpcSenderConfig, int cacheSize, int sendBufSize) {
        super(rpcSenderConfig, cacheSize, sendBufSize);

    }

    @Override
    protected void doSend(E aggregatedEntity, RpcSenderConfig rpcSenderConfig) {

    }
}
