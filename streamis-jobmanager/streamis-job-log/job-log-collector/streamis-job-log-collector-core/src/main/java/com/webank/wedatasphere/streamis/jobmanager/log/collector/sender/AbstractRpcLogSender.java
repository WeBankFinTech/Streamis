package com.webank.wedatasphere.streamis.jobmanager.log.collector.sender;

import com.webank.wedatasphere.streamis.jobmanager.log.collector.ExceptionListener;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.cache.LogCache;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.RpcLogSenderConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.config.SendLogCacheConfig;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.buf.ImmutableSendBuffer;
import com.webank.wedatasphere.streamis.jobmanager.log.collector.sender.buf.SendBuffer;
import com.webank.wedatasphere.streamis.jobmanager.log.entities.LogElement;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Abstract rpc log sender
 * @param <T>
 * @param <E>
 */
public abstract class AbstractRpcLogSender<T extends LogElement, E> implements RpcLogSender<T>{

    /**
     * Size of log cache
     */
    int cacheSize;

    /**
     * The buffer size of sender
     */
    int sendBufSize;

    /**
     * Max thread num of send
     */
    int maxCacheConsume;

    /**
     * Connect config
     */
    protected RpcLogSenderConfig rpcSenderConfig;

    /**
     * Rpc log context
     */
    private  RpcLogContext rpcLogContext;

    protected boolean isTerminated = false;
    /**
     * Use the listener instead of log4j structure
     */
    protected ExceptionListener exceptionListener;


    protected AbstractRpcLogSender(RpcLogSenderConfig rpcSenderConfig){
        this.rpcSenderConfig = rpcSenderConfig;
        SendLogCacheConfig cacheConfig = rpcSenderConfig.getCacheConfig();
        this.cacheSize = cacheConfig.getSize();
        this.maxCacheConsume = cacheConfig.getMaxConsumeThread();
        this.sendBufSize = rpcSenderConfig.getBufferConfig().getSize();
        if (sendBufSize > cacheSize) {
            throw new IllegalArgumentException("Size of send buffer is larger than cache size");
        }

    }

    @Override
    public  LogCache<T> getOrCreateLogCache() {
        return getOrCreateRpcLogContext().getLogCache();
    }

    @Override
    public void sendLog(T log) {
        // Just send it into log cache
        try {
            getOrCreateLogCache().cacheLog(log);
        } catch (InterruptedException e) {
            // Invoke exception listener
            Optional.ofNullable(exceptionListener).ifPresent(listener ->
                    listener.onException(this, e, null));
        }
    }

    @Override
    public void syncSendLog(T log) {

    }

    @Override
    public void setExceptionListener(ExceptionListener listener) {
        this.exceptionListener = listener;
    }

    @Override
    public void close() {
        getOrCreateRpcLogContext().destroyCacheConsumers();
        this.isTerminated = true;
    }

    /**
     * Aggregate send buffer for sending
     * @param sendBuffer send buffer
     * @return E aggregated entity
     */
    protected abstract E aggregateBuffer(SendBuffer<T> sendBuffer);

    /**
     * Sending operation
     * @param aggregatedEntity agg entity
     * @param rpcSenderConfig rpc sender config
     */
    protected abstract void doSend(E aggregatedEntity, RpcLogSenderConfig rpcSenderConfig) throws IOException;

    /**
     * Send log exception strategy
     * @return exception strategy
     */
    protected abstract SendLogExceptionStrategy<T> getSendLogExceptionStrategy();

    protected synchronized RpcLogContext getOrCreateRpcLogContext() {
        if (null == this.rpcLogContext) {
            // Use fair lock
            SendLogCache<T> logCache = new QueuedSendLogCache(this.cacheSize,
                    this.rpcSenderConfig.getCacheConfig().isDiscard(),
                    this.rpcSenderConfig.getCacheConfig().getDiscardWindow() * 1000, false);
            RpcLogContext context = new RpcLogContext(logCache);
            // Start cache consumers
            for (int i = 0; i < maxCacheConsume; i++) {
                context.startCacheConsumer();
            }
            this.rpcLogContext =context;
        }
        return this.rpcLogContext;
    }

    private class RpcLogContext{

        private static final String RPC_LOG_CACHE_CONSUMER = "RpcLog-Cache-Consumer-Thread-";
        /**
         * Send log cache
         */
        private final SendLogCache<T> logCache;

        /**
         * Consume pool
         */
        private final ThreadPoolExecutor consumePool;

        /**
         * Count of the consumers
         */
        private int consumers = 0;

        /**
         * Futures of consumers
         */
        private final LinkedList<SendLogCacheConsumer<T>> sendLogCacheConsumers = new LinkedList<>();
        /**
         * Context lock
         */
        private final ReentrantLock ctxLock;
        public RpcLogContext(SendLogCache<T> logCache){
            this.logCache = logCache;
            this.ctxLock = new ReentrantLock();
            this.consumePool = new ThreadPoolExecutor(0, maxCacheConsume,
                    60L, TimeUnit.SECONDS,
                    new SynchronousQueue<>(), new ThreadFactory() {
                private final ThreadGroup group = Thread.currentThread().getThreadGroup();
                private final AtomicInteger threadNum = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(group, r, RPC_LOG_CACHE_CONSUMER
                            + threadNum.getAndIncrement(), 0);
                    if (t.isDaemon()) {
                        t.setDaemon(false);
                    }
                    if (t.getPriority() != Thread.NORM_PRIORITY) {
                        t.setPriority(Thread.NORM_PRIORITY);
                    }
                    return t;
                }
            });
        }

        public boolean startCacheConsumer(){
            if (consumers >= maxCacheConsume) {

                return false;
            }
            this.ctxLock.lock();
            try {
                if (consumers < maxCacheConsume) {
                    String id = UUID.randomUUID().toString();
                    SendBuffer<T> sendBuffer = new ImmutableSendBuffer<>(sendBufSize);
                    SendLogCacheConsumer<T> consumer = new SendLogCacheConsumer<T>(id, logCache, sendBuffer, rpcSenderConfig) {
                        @Override
                        protected void onFlushAndSend(SendBuffer<T> sendBuffer) {
                            // First to aggregate the buffer
                            E aggEntity = aggregateBuffer(sendBuffer);
                            Optional.ofNullable(getSendLogExceptionStrategy()).ifPresent(
                                    strategy -> strategy.doSend(() -> {
                                        doSend(aggEntity, rpcSenderConfig);
                                        return null;
                                    }, sendBuffer));
                        }
                    };
                    Future<?> future = this.consumePool.submit(consumer);
                    consumer.setFuture(future);
                    sendLogCacheConsumers.add(consumer);
                    this.consumers++;
                    return true;
                }
            } finally {
                this.ctxLock.unlock();
            }
            return false;
        }

        public SendLogCache<T> getLogCache(){
            return this.logCache;
        }

        /**
         * Destroy cache consumer(select the tail one)
         */
        public boolean destroyCacheConsumer(){
            if (this.consumers <= 1){
                return false;
            }
            this.ctxLock.lock();
            try {
                if (this.consumers > 1 && this.sendLogCacheConsumers.size() > 1) {
                    SendLogCacheConsumer<T> consumer = sendLogCacheConsumers.removeLast();
                    consumer.shutdown();
                    this.consumers --;
                    return true;
                }
            } finally {
                this.ctxLock.unlock();
            }
            return false;
        }

        /**
         * Destroy all the consumers
         */
        public void destroyCacheConsumers(){
            this.ctxLock.lock();
            try {
                sendLogCacheConsumers.forEach(SendLogCacheConsumer::shutdown);
                sendLogCacheConsumers.clear();
                this.consumers = 0;
            } finally {
                this.ctxLock.unlock();
            }
        }
    }
    /**
     * Act as ArrayBlockingQueue (jdk 1.8)
     */
    private class QueuedSendLogCache implements SendLogCache<T>{

        // Queued items
        final Object[] items;

        // Take index
        int takeIndex;

        // Put index
        int putIndex;

        // Count
        int count;

        // Wait time in caching
        final AtomicLong cacheWaitTime = new AtomicLong(0);

        // Wait time in taking
        final AtomicLong takeWaitTime = new AtomicLong(0);

        // Performance of processing
        final AtomicLong process = new AtomicLong(0);

        // Control flow
        final AtomicLong control = new AtomicLong(Long.MAX_VALUE - 1);

        // If enable to discard log
        boolean discard;

        int discardCount = 0;

        // Time clock
        long clock = System.currentTimeMillis();

        // interval to control
        long controlInterval = 1 * 1000L;

        // Reentrant lock
        final ReentrantLock lock;

        // Condition for waiting takes
        private final Condition notEmpty;

        // Condition for waiting puts(cacheLog)
        private final Condition notFull;

        public QueuedSendLogCache(int capacity, boolean discard, int discardWind, boolean fair) {
            this.items = new Object[capacity];
            lock = new ReentrantLock(fair);
            this.notEmpty = lock.newCondition();
            this.notFull = lock.newCondition();
            this.discard = discard;
            // Make the discard window size as the control interval
            this.controlInterval = discardWind;
            this.clock = System.currentTimeMillis() + controlInterval;
        }

        @Override
        public void cacheLog(T logElement) throws InterruptedException {
            // Skip the null element
            if (Objects.nonNull(logElement)){
                final ReentrantLock lock = this.lock;
                boolean tryLock = lock.tryLock();
                if (!tryLock){
                    lock.lockInterruptibly();
                }
                try{
                    flowControl();
                    if (discard && control.decrementAndGet() <= 0 && logElement.mark() < 2){
                            discardCount++;
                            return;
                    }
                    while (count == items.length){
                        long ws = System.currentTimeMillis();
                        notFull.await();
                        cacheWaitTime.addAndGet(System.currentTimeMillis() - ws);
                    }
                    enqueue(logElement);
                    if (rpcSenderConfig.isDebugMode()) {
                        System.out.println("cache log at time: " + logElement.getLogTimeStamp());
                    }
                }finally{
                    lock.unlock();
                }
            }
        }

        @Override
        public int drainLogsTo(List<T> elements, int maxElements) {
            if (Objects.nonNull(elements) && maxElements > 0){
                final Object[] items = this.items;
                final ReentrantLock lock = this.lock;
                lock.lock();
                try{
                    int n = Math.min(maxElements, count);
                    int take = takeIndex;
                    int i = 0;
                    try {
                        while (i < n){
                            @SuppressWarnings("unchecked")
                            T x = (T) items[take];
                            elements.add(x);
                            items[take] = null;
                            if (++ take == items.length)
                                take = 0;
                            i++;
                        }
                        return n;
                    }finally {
                        restoreInvariants(i, take, false);
                    }
                } finally {
                    lock.unlock();
                }
            }
            return 0;
        }

        // Equal to the poll method in ArrayBlockingQueue
        @Override
        public T takeLog(long timeout, TimeUnit unit) throws InterruptedException {
            long nanos = unit.toNanos(timeout);
            final ReentrantLock lock = this.lock;
            T element;
            lock.lockInterruptibly();
            try{
                flowControl();
                while (count == 0){
                    long ws = System.currentTimeMillis();
                    if (nanos <= 0){
                        return null;
                    }
                    nanos = notEmpty.awaitNanos(nanos);
                    takeWaitTime.addAndGet(System.currentTimeMillis() - ws);
                }
                element = dequeue();
                process.incrementAndGet();
                if (rpcSenderConfig.isDebugMode()) {
                    System.out.println("cache log at time: " + element.getLogTimeStamp());
                }
            } finally {
                lock.unlock();
            }
            return element;
        }

        @Override
        public boolean isCacheable() {
            final ReentrantLock lock = this.lock;
            lock.lock();
            try {
                return count < items.length;
            }finally {
                lock.unlock();
            }
        }

        // The same as the clear() method,
        @Override
        public void destroy() {
            final Object[] items = this.items;
            final ReentrantLock lock = this.lock;
            lock.lock();
            try {
                int k = count;
                if (k > 0) {
                    final int putIndex = this.putIndex;
                    int i = takeIndex;
                    do {
                        items[i] = null;
                        if (++i == items.length)
                            i = 0;
                    } while (i != putIndex);
                    takeIndex = putIndex;
                    count = 0;
                    for (; k > 0 && lock.hasWaiters(notFull); k--)
                        notFull.signal();
                }
            } finally {
                lock.unlock();
            }
        }

        /**
         * Drain the elements into send buffer
         * @param sendBuffer send buffer
         * @param maxElements max element size
         * @return int
         */
        @Override
        public int drainLogsTo(SendBuffer<T> sendBuffer, int maxElements) {
            if (Objects.nonNull(sendBuffer) && maxElements > 0){
                final Object[] items = this.items;
                final ReentrantLock lock = this.lock;
                lock.lock();
                try{
                    flowControl();
                    int n = Math.min(maxElements, count);
                    int take = takeIndex;
                    int i = 0;
                    int send;
                    try {
                        while (n > 0) {
                            int len = items.length - take;
                            int send0 = Math.min(n, len);
                            // Copy the array element to buffer directly
                            send = sendBuf(sendBuffer, this.items, take, send0);
                            n -= send;
                            if ((take = take + send) >= items.length) {
                                take = 0;
                            }
                            i += send;
                            if (send < send0 || send <= 0) {
                                break;
                            }
                        }
                        process.addAndGet(i);
                        return i;
                    } finally {
                        if (i > 0){
                            restoreInvariants(i, take, true);
                        }
                    }
                }finally {
                    lock.unlock();
                }
            }
            return 0;
        }

        @SuppressWarnings("unchecked")
        private int sendBuf(SendBuffer<T> sendBuffer, Object[] items, int takeIndex, int len){
            int send = sendBuffer.writeBuf(items, takeIndex, len);
            if (send < len){
                // Buffer full exception
                exceptionListener.onException(this, null, "The sender buffer is full," +
                        " expected: [" + len + "], actual: [" + send + "]");
            }
            // Allow data loss
            return send;
        }

        private void restoreInvariants(int i, int take, boolean clearItems){
            this.count -= i;
            if (clearItems){
                int index = this.takeIndex;
                int j = i;
                for (; j > 0; j --){
                    this.items[index] = null;
                    if (++index == items.length){
                        index = 0;
                    }
                }
                //At last index equals take
            }
            this.takeIndex = take;
            for (; i > 0 && lock.hasWaiters(notFull); i--){
                notFull.signal();
            }
        }
        // Inserts element at current put position, advances, and signals. Call only when holding lock.
        private void enqueue(T element){
            this.items[putIndex] = element;
            if (++putIndex >= items.length){
                putIndex = 0;
            }
            count ++;
            notEmpty.signal();
        }

        // Extracts element at current take position, advances, and signals. Call only when holding lock.
        private T dequeue(){
            @SuppressWarnings("unchecked")
            T element = (T)this.items[takeIndex];
            this.items[takeIndex] = null;
            if ( ++ takeIndex == items.length){
                this.takeIndex = 0;
            }
            count --;
            // Not need to support iterator
            notFull.signal();
            return element;
        }

        /**
         * Flow control
         */
        private void flowControl(){
            long ws = System.currentTimeMillis();
            if (clock <= ws) {
                long interval = ws - clock + controlInterval;
                clock = ws + controlInterval;
                if (rpcSenderConfig.isDebugMode()) {
                    System.out.println("cacheWait: " + cacheWaitTime.get() + ", takeWait:" + takeWaitTime.get() + ", discarded: " + discardCount);
                }
                if (takeWaitTime.get() <= 0 && process.get() > 0){
                    this.control.set((long) ((double)process.get() * ((double)controlInterval / (double)interval)));
                    if (rpcSenderConfig.isDebugMode()) {
                        System.out.println("new window control: " + this.control.get());
                    }
                } else {
                    this.control.set(Long.MAX_VALUE);
                }
                cacheWaitTime.set(0);
                takeWaitTime.set(0);
                process.set(0);
                discardCount = 0;
            }
        }
    }
}
