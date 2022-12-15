package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.buf.SendBuffer;
import com.webank.wedatasphere.streamis.jobmanager.log.entities.LogElement;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * Strategy control the action on exception
 */
public abstract class SendLogExceptionStrategy<T extends LogElement> {

    protected final RpcLogSender<T> sender;

    public SendLogExceptionStrategy(RpcLogSender<T> sender){
        this.sender = sender;
    }
    /**
     * Retry count
     * @return retry
     */
    public abstract int retryCount();

    /**
     *
     * @param e exception
     * @return boolean
     */
    public abstract RetryDescription onException(Exception e, SendBuffer<T> sendBuffer);

    <V>V doSend(Callable<V> sendOperation, SendBuffer<T> sendBuffer){
        int retryCount = retryCount();
        int count = 0;
        RetryDescription retryDescription;
        while (++count <= retryCount) {
            try {
                return sendOperation.call();
            } catch (Exception e) {
                retryDescription = onException(e, sendBuffer);
                if (Objects.isNull(retryDescription) || !retryDescription.canRetry) {
                    break;
                }
            }
        }
        return null;
    }

    protected static class RetryDescription{

        private final boolean canRetry;

        public RetryDescription(boolean canRetry){
            this.canRetry = canRetry;
        }

        public boolean isCanRetry() {
            return canRetry;
        }
    }
}
