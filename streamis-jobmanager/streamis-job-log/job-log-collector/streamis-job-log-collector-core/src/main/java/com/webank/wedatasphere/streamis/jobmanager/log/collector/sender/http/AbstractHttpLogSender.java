package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.http;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.RpcAuthConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.AbstractRpcLogSender;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.RpcLogSenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.SendLogExceptionStrategy;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.buf.SendBuffer;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.http.request.StringPostAction;
import com.webank.wedatasphere.streamis.jobmanager.log.entities.LogElement;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.conn.ConnectTimeoutException;

import javax.net.ssl.SSLException;
import java.io.*;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Optional;
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

    protected AbstractHttpLogSender(RpcLogSenderConfig rpcSenderConfig) {
        super(rpcSenderConfig);
        this.globalHttpClient = HttpClientTool.createHttpClient(rpcSenderConfig);
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
                        if (retryOnException.equals(e.getClass())) {
                            shouldRetry = true;
                            break;
                        }
                    }
                    if (!shouldRetry && e instanceof HttpResponseException && ((HttpResponseException) e).getStatusCode() < 500){
                            shouldRetry = true;
                    }
                }
                if (shouldRetry && !sender.getOrCreateLogCache().isCacheable()){
                    // Means that the cache is full
                    // Set the position of buffer to 0
                    sendBuffer.rewind();
                    sendBuffer.compact( element -> element.mark() > 1);
                    shouldRetry = false;
                }
                Optional.ofNullable(exceptionListener).ifPresent(listener -> listener.onException(sender, e, null));
                return new RetryDescription(shouldRetry);
            }
        };
    }

    @Override
    protected SendLogExceptionStrategy<T> getSendLogExceptionStrategy() {
        return this.sendRetryStrategy;
    }

    @Override
    protected void doSend(E aggregatedEntity, RpcLogSenderConfig rpcSenderConfig) throws IOException {
        if (System.currentTimeMillis() >= serverRecoveryTimePoint.get()) {
            if (aggregatedEntity instanceof LogElement) {
                long timestamp = ((LogElement) aggregatedEntity).getLogTimeStamp();
                if (System.currentTimeMillis() - timestamp > rpcSenderConfig.getMaxDelayTimeInSec() * 1000L) {
                    // Abort the entity
                    return;
                }
            }
            httpResponse(aggregatedEntity,rpcSenderConfig);
        }
    }

    private void httpResponse(E aggregatedEntity,RpcLogSenderConfig rpcSenderConfig) throws IOException {
        String address = rpcSenderConfig.getAddress();
        if (null != address && !address.trim().equals("")) {
            StringPostAction postAction = new StringPostAction(rpcSenderConfig.getAddress(), convertToJsonString(aggregatedEntity));
            RpcAuthConfig authConfig = rpcSenderConfig.getAuthConfig();
            postAction.getRequestHeaders().put(authConfig.getTokenUserKey(), authConfig.getTokenUser());
            HttpResponse response = null;
            try {
                response = postAction.execute(this.globalHttpClient);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode > 200){
                    throw new HttpResponseException(statusCode,
                            convertToString(response.getEntity().getContent(), StandardCharsets.UTF_8));
                }
            }finally {
                // Close the response and release the conn
                if (null != response){
                    if (response instanceof CloseableHttpResponse){
                        ((CloseableHttpResponse)response).close();
                    } else {
                        // Destroy the stream
                        response.getEntity().getContent().close();
                    }
                }
            }
            // Init the counter
            this.exceptionCounter.set(0);
        }
    }
    /**
     * Convert input to string
     * @param inputStream input stream
     * @param charset charset
     * @return string value
     * @throws IOException
     */
    private String convertToString(InputStream inputStream, Charset charset) throws IOException {
        StringBuilder builder = new StringBuilder();
        try(BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, charset))){
            String line;
            while((line = reader.readLine()) != null){
                builder.append(line);
            }
        }
        return builder.toString();
    }

    /**
     * Convert the entity to json
     * @param aggregatedEntity aggregated entity
     * @return json string
     */
    protected abstract String convertToJsonString(E aggregatedEntity);
}
