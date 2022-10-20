package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.http;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.AbstractRpcLogSender;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.RpcLogSender;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.RpcSenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.SendLogExceptionStrategy;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.buf.SendBuffer;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.http.request.EntityPostAction;
import com.webank.wedatasphere.streamis.jobmanager.log.entities.LogElement;
import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ConnectTimeoutException;

import javax.net.ssl.SSLException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractHttpLogSender<T extends LogElement, E> extends AbstractRpcLogSender<T, E> {

    /**
     * Retry strategy
     */
    private final SendLogExceptionStrategy<T> sendRetryStrategy;

    /**
     * Exception counter
     */
    private final AtomicInteger exceptionCounter = new AtomicInteger();
    /**
     * Hold the global http client
     */
    private final HttpClient globalHttpClient;

    /**
     * Recover time point
     */
    private final AtomicLong serverRecoveryTimePoint = new AtomicLong(-1L);

    public AbstractHttpLogSender(RpcSenderConfig rpcSenderConfig, int cacheSize, int sendBufSize) {
        super(rpcSenderConfig, cacheSize, sendBufSize);
        this.globalHttpClient = HttpClientTool.createHttpClient(rpcSenderConfig);
        final RpcLogSender<T> sender = this;
        this.sendRetryStrategy = new SendLogExceptionStrategy<T>(this) {

            private final Class<?>[] retryOnExceptions = new Class<?>[]{
                    InterruptedIOException.class, UnknownHostException.class,
                    ConnectTimeoutException.class, SSLException.class};
            @Override
            public int retryCount() {
                return rpcSenderConfig.getSendRetryCnt();
            }

            @Override
            public SendLogExceptionStrategy.RetryDescription onException(Exception e, SendBuffer<T> sendBuffer) {
                boolean shouldRetry = false;
                // Limit of exception number is the same as the retry times
                if (exceptionCounter.incrementAndGet() > retryCount()){
                    serverRecoveryTimePoint.set(System.currentTimeMillis() +
                            TimeUnit.SECONDS.toMillis(rpcSenderConfig.getServerRecoveryTimeInSec()));
                } else {
                    for (Class<?> retryOnException : retryOnExceptions) {
                        if (retryOnException.isAssignableFrom(e.getClass())) {
                            shouldRetry = true;
                            break;
                        }
                    }
                }
                if (shouldRetry && !sender.getOrCreateLogCache().isCacheable()){
                    // Means that the cache is full
                    // Set the position of buffer to 0
                    sendBuffer.rewind();
                    // Compact the buffer and transient to write mode;
                    sendBuffer.compact( element -> element.mark() > 1);
                    shouldRetry = false;
                }
                exceptionListener.onException(sender, e, null);
                return new RetryDescription(shouldRetry);
            }
        };
    }

    @Override
    protected SendLogExceptionStrategy<T> getSendLogExceptionStrategy() {
        return this.sendRetryStrategy;
    }

    @Override
    protected void doSend(E aggregatedEntity, RpcSenderConfig rpcSenderConfig) throws IOException {
        if (System.currentTimeMillis() >= serverRecoveryTimePoint.get()) {
            if (aggregatedEntity instanceof LogElement) {
                long timestamp = ((LogElement) aggregatedEntity).getLogTimeStamp();
                if (System.currentTimeMillis() - timestamp > rpcSenderConfig.getMaxDelayTimeInSec() * 1000L) {
                    // Abort the entity
                    return;
                }
            }
            EntityPostAction<E> postAction = new EntityPostAction<>(rpcSenderConfig.getAddress(), aggregatedEntity);
            postAction.getRequestHeaders().put(rpcSenderConfig.getTokenUserKey(), rpcSenderConfig.getTokenUser());
            // Ignore the response
            postAction.execute(this.globalHttpClient);
            // Init the counter
            this.exceptionCounter.set(0);
        }
    }
}
