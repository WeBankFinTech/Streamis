package com.webank.wedatasphere.streamis.jobmanager.log.server.storage.bucket;

import com.webank.wedatasphere.streamis.jobmanager.log.entities.LogElement;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.context.JobLogStorageContext;
import com.webank.wedatasphere.streamis.jobmanager.log.server.storage.utils.RegularUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.common.conf.CommonVars;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.*;
import org.apache.logging.log4j.core.appender.rolling.action.*;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Job log bucket for log4j
 */
public class Log4j2JobLogBucket implements JobLogBucket{

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Log4j2JobLogBucket.class);

    private static final String DEFAULT_FILE_PATTERN_SUFFIX = ".%d{yyyy-MM-dd}-%i";

    private static final CommonVars<Integer> ROLLOVER_MAX = CommonVars.apply("wds.stream.job.log.storage.bucket.log4j.rollover-max", 20);
    /**
     * Bucket name
     */
    private final String bucketName;

    /**
     * Logger name
     */
    private final String loggerName;
    /**
     * Logger context
     */
    private final LoggerContext loggerContext;

    /**
     * Logger entity
     */
    private final Logger logger;

    /**
     * Storage context
     */
    private final JobLogStorageContext storageContext;
    /**
     * Storage writer
     */
    private final JobLogStorageWriter jobLogStorageWriter;

    /**
     * Bucket state
     */
    private final JobLogBucketState jobLogBucketState;

    /**
     * Last write time;
     */
    private long lastWriteTime;

    /**
     * Prev Interval time
     */
    private long preIntervalTime;

    /**
     * Active thread
     */
    private final AtomicLong activeThread = new AtomicLong(0);
    /**
     * Interval counter
     */
    private final AtomicLong intervalCounter = new AtomicLong(0);

    /**
     * Shutdown flag
     */
    private final AtomicBoolean isShutdown = new AtomicBoolean(false);

    /**
     * Shutdown lock
     */
    private final ReentrantLock shutdownLock = new ReentrantLock();

    /**
     * Shutdown condition
     */
    private final Condition canShutdown = shutdownLock.newCondition();
    /**
     * Store the write rate
     */
    private double writeRate;
    public Log4j2JobLogBucket(String bucketName, JobLogStorageContext storageContext, JobLogBucketConfig config){
        this.bucketName = bucketName;
        // Build unique logger name
        this.loggerName = bucketName + System.currentTimeMillis() + "_" + Thread.currentThread().getId();
        this.storageContext = storageContext;
        // Create logger context
        this.loggerContext = (LoggerContext) LogManager.getContext(false);
        this.logger = initLogger(this.bucketName, this.loggerName, this.storageContext, config, this.loggerContext);
        this.jobLogStorageWriter = createStorageWriter();
        this.jobLogBucketState = createBucketState();
    }
    @Override
    public JobLogBucketState getBucketState() {
        return this.jobLogBucketState;
    }

    @Override
    public JobLogStorageWriter getBucketStorageWriter() {
        return this.jobLogStorageWriter;
    }

    @Override
    public JobLogStorageContext getStorageContext() {
        return this.storageContext;
    }

    @Override
    public String getBucketName() {
        return this.bucketName;
    }

    @Override
    public void close() {
       this.isShutdown.set(true);
       this.shutdownLock.lock();
       try{
           if (activeThread.get() > 0 && !this.canShutdown.await(5, TimeUnit.SECONDS)) {
               LOG.warn("Shutdown the bucket: [{}] directly because the timeout of waiting", bucketName);
           }
       } catch (InterruptedException e) {
           // Ignore
           
       } finally {
           this.shutdownLock.unlock();
       }
       Configuration log4jConfig =  this.loggerContext.getConfiguration();
       // First to stop appender
       log4jConfig.getAppender(this.loggerName).stop();
       log4jConfig.getLoggerConfig(this.loggerName).removeAppender(this.loggerName);
       log4jConfig.removeLogger(this.loggerName);
       loggerContext.updateLoggers();
    }

    private synchronized Logger initLogger(String bucketName, String loggerName,
                                           JobLogStorageContext storageContext, JobLogBucketConfig config, LoggerContext loggerContext){
        Configuration log4jConfig = loggerContext.getConfiguration();
        String fileName = resolveFileName(storageContext.getStorePath().toString(), bucketName);
        RollingFileAppender appender = RollingFileAppender.newBuilder()
                .setLayout(PatternLayout.newBuilder().withPattern(config.getLogLayOutPattern()).build())
                .setName(loggerName)
//                .withFileOwner()
                .withFileName(fileName)
                .withFilePattern(resolveFilePattern(fileName, config.getBucketPartCompress()))
                .withPolicy(SizeBasedTriggeringPolicy.createPolicy(config.getMaxBucketActivePartSize() + "MB"))
                .withStrategy(createRolloverStrategy(log4jConfig, fileName, ROLLOVER_MAX.getValue(), config.getBucketPartHoldTimeInDay()))
                .setConfiguration(log4jConfig)
                .build();
        appender.start();
        log4jConfig.addAppender(appender);
        LoggerConfig loggerConfig = LoggerConfig.newBuilder().withAdditivity(false).withLevel(Level.ALL)
                .withRefs(new AppenderRef[]{
                        AppenderRef.createAppenderRef(loggerName, null, null)
                })
                .withLoggerName(loggerName).withConfig(log4jConfig).build();
        loggerConfig.addAppender(appender, null, null);
        log4jConfig.addLogger(loggerName, loggerConfig);
        // Should we update the logger context ?
        loggerContext.updateLoggers();
        return loggerContext.getLogger(loggerName);
    }

    /**
     * Create storage writer
     * @return storage writer
     */
    private JobLogStorageWriter createStorageWriter(){
        return new JobLogStorageWriter() {
            @Override
            public <T extends LogElement> void write(LogElement logEl) {
                activeThread.incrementAndGet();
                try {
                    String[] contents = logEl.getContents();
                    if (null != contents) {
                        for (String content : contents) {
                            write(content, true);
                        }
                    }
                }finally {
                    if (activeThread.decrementAndGet() <= 0 && isShutdown.get()){
                        notifyShutdown();
                    }
                }
            }

            @Override
            public void write(String logLine) {
                activeThread.incrementAndGet();
                try {
                    write(logLine, false);
                }finally {
                    if (activeThread.decrementAndGet() <= 0 && isShutdown.get()){
                        notifyShutdown();
                    }
                }
            }

            private void write(String logLine, boolean batch){
                logger.info(logLine);
                long currentTime = System.currentTimeMillis();
                long intervalCnt = intervalCounter.getAndIncrement();
                long intervalTime = (currentTime - preIntervalTime)/1000;
                // Per minute accumulate the rate
                if ( intervalTime >= 60){
                    writeRate = (double)intervalCnt / (double)intervalTime;
                    preIntervalTime = currentTime;
                    intervalCounter.set(0);
                }
                lastWriteTime = currentTime;
            }
            @Override
            public void close() {
                // Ignore
            }
        };
    }

    /**
     * Create bucket state
     * @return bucket state
     */
    private JobLogBucketState createBucketState(){
        return new JobLogBucketState() {
            private String bucketPath;
            @Override
            public String getBucketPath() {
                if (StringUtils.isBlank(bucketPath)) {
                    Appender appender = loggerContext.getConfiguration().getAppender(loggerName);
                    if (appender instanceof RollingFileAppender) {
                        bucketPath =  new File(((RollingFileAppender) appender).getFileName()).getParent();
                    }
                }
                return this.bucketPath;
            }

            @Override
            public double getBucketWriteRate() {
                return writeRate;
            }

            @Override
            public int getBucketParts() {
                AtomicInteger parts = new AtomicInteger(-1);
                String path = getBucketPath();
                if (StringUtils.isNotBlank(path)){
                    Optional.ofNullable(new File(path).list()).ifPresent(list -> parts.set(list.length));
                }
                return parts.get();
            }

            @Override
            public long getBucketWriteTime() {
                return lastWriteTime;
            }
        };
    }

    private synchronized void notifyShutdown(){
        this.shutdownLock.lock();
        try{
            this.canShutdown.notifyAll();
        }finally {
            this.shutdownLock.unlock();
        }
    }
    /**
     * Create rollover strategy
     * @param configuration configuration
     * @param fileName file name
     * @param rolloverMax rollover max inf file pattern
     * @param fileHoldDay file hold day time
     * @return strategy
     */
    private RolloverStrategy createRolloverStrategy(Configuration configuration,
                                                    String fileName, int rolloverMax, int fileHoldDay){
        DefaultRolloverStrategy.Builder builder = DefaultRolloverStrategy.newBuilder();
        if (rolloverMax > 0){
            builder.withMax(rolloverMax + "");
        }
        if (fileHoldDay > 0){
            // Create the actions to delete old file
            builder.withCustomActions(new Action[]{
                            DeleteAction.createDeleteAction(new File(FilenameUtils.normalize(fileName)).getParent(), false, 2, false, null,
                                    new PathCondition[]{
                                            IfFileName.createNameCondition(null, ".*"),
                                            IfLastModified.createAgeCondition(Duration.parse(fileHoldDay + "d"))
                                    },
                                    null, configuration)
                    }
            );
        }
        return  builder.build();
    }
    /**
     * Ex: /data/stream/log/hadoop/{projectName}/{jobName}/{projectName}.{jobName}.log
     * @param bucketRootPath bucket root path
     * @param bucketName bucket name
     * @return file name with absolute path
     */
    private String resolveFileName(String bucketRootPath, String bucketName){
        String fileName = FilenameUtils.normalize(bucketName);
        String basePath = bucketRootPath;
        if (!basePath.endsWith("/")){
            basePath += "/";
        }
        basePath += fileName.replace(".", "/");
        String[] fileNameArray = RegularUtils.split(fileName);
        if (fileNameArray.length > 2){
            fileName = fileNameArray[0] + "." + fileNameArray[2];
        }
        return basePath + "/" + fileName.substring(bucketName.indexOf(".") + 1) + ".log";
    }

    /**
     * Resolve file pattern
     * @param fileName file name
     * @param format format
     * @return file pattern
     */
    private String resolveFilePattern(String fileName, String format){
        String filePattern = fileName + Log4j2JobLogBucket.DEFAULT_FILE_PATTERN_SUFFIX;
        if (StringUtils.isNotBlank(format)){
            filePattern = filePattern +  (format.startsWith(".") ? format : "." +format);
        }
        return filePattern;
    }

}
