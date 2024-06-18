package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.RpcLogSenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.buf.SendBuffer;
import com.webank.wedatasphere.streamis.jobmanager.log.entities.LogElement;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Send log consumer
 * Consume the log elements from cache and put into send buffer
 * @param <T>
 */
public abstract class SendLogCacheConsumer<T extends LogElement> implements Runnable{


    private boolean isTerminated = false;

    /**
     * Buffer expire time in milliseconds
     */
    private final long bufferExpireTimeInMills;
    /**
     * Send log cache
     */
    private final SendLogCache<T> cache;

    /**
     * Send buffer
     */
    private final SendBuffer<T> sendBuffer;

    private final String id;

    /**
     * Future for execution
     */
    private Future<?> future;

    protected SendLogCacheConsumer(String id, SendLogCache<T> cache,
                                SendBuffer<T> sendBuffer,
                                RpcLogSenderConfig rpcSenderConfig){
        this.id = id;
        this.cache = cache;
        this.sendBuffer = sendBuffer;
        long expireTimeInSec = rpcSenderConfig.getBufferConfig().getExpireTimeInSec();
        this.bufferExpireTimeInMills = expireTimeInSec > 0 ? TimeUnit.SECONDS
                .toMillis(expireTimeInSec) : -1;
        System.out.println("SendLogCacheConsumer with id : " + id + " started.");

    }

    @Override
    public void run() {
        int remain;
        long expireTimeInMills = requireNewFlushTime();
        int capacity = sendBuffer.capacity();
        while (!this.isTerminated) {
            try {
                remain = this.sendBuffer.remaining();
                if ((expireTimeInMills > 0 && expireTimeInMills <= System.currentTimeMillis()) || remain <= 0) {
                    // Transient to the read mode
                    if (remain < capacity) {
                        sendBuffer.flip();
                        onFlushAndSend(sendBuffer);
                    }
                    expireTimeInMills = requireNewFlushTime();
                    if (sendBuffer.isReadMode()) {
                        // Clear the buffer and transient to the write mode, otherwise continue writing
                        sendBuffer.clear();
                    }
                    remain = this.sendBuffer.remaining();
                }
                if (remain > 0) {
                    int inBuf = this.cache.drainLogsTo(sendBuffer, remain);
                    if (inBuf < remain) {
                        // Means that the cache is empty, take and wait the log element
                        long waitTime = expireTimeInMills - System.currentTimeMillis();
                        if (waitTime > 0) {
                            T logElement = this.cache.takeLog(waitTime, TimeUnit.MILLISECONDS);
                            if (null != logElement) {
                                sendBuffer.writeBuf(logElement);
                            }
                        }
                    }
                }
            } catch (Exception e){
                if (this.isTerminated && e instanceof InterruptedException){
                    System.out.println("SendLogCacheConsumer[" + Thread.currentThread().getName() + "] with id : " + id + " isTerminated or Interrupted.");
                    return;
                } else {
                    e.printStackTrace();
                    System.err.println("SendLogCacheConsumer[" + Thread.currentThread().getName() + "] occurred exception [" + e.getLocalizedMessage() + "]");
                    // For the unknown exception clear the cache
                   sendBuffer.clear();
                   expireTimeInMills = requireNewFlushTime();
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void shutdown(){
        this.isTerminated = true;
        if (null != this.future){
            this.future.cancel(true);
        }
    }

    public Future<?> getFuture() {
        return future;
    }

    public void setFuture(Future<?> future) {
        this.future = future;
    }

    private long requireNewFlushTime(){
        return bufferExpireTimeInMills > 0 ? System.currentTimeMillis() + bufferExpireTimeInMills : -1;
    }
    /**
     * When the buffer is full or reach the idle time, invoke the method
     * @param sendBuffer send buffer
     */
    protected abstract void onFlushAndSend(SendBuffer<T> sendBuffer);

    public String getId() {
        return id;
    }
}
