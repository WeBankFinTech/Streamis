package com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.handler;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.webank.wedatasphere.streamis.jobmanager.launcher.linkis.manager.StreamisErrorCodeManager;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.StringUtils;
import org.apache.linkis.errorcode.client.ClientConfiguration;
import org.apache.linkis.errorcode.client.handler.ExceptionErrorCodeHandler;
import org.apache.linkis.errorcode.client.handler.LinkisErrorCodeHandler;
import org.apache.linkis.errorcode.client.handler.LogErrorCodeHandler;
import org.apache.linkis.errorcode.client.handler.LogFileErrorCodeHandler;
import org.apache.linkis.errorcode.client.synchronizer.LinkisErrorCodeSynchronizer;
import org.apache.linkis.errorcode.client.utils.ErrorCodeMatcher;
import org.apache.linkis.errorcode.common.ErrorCode;
import org.apache.linkis.errorcode.common.LinkisErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;
import scala.Tuple2;
import java.util.*;
import java.util.concurrent.*;

public class StreamisErrorCodeHandler extends LinkisErrorCodeHandler {

    private static StreamisErrorCodeHandler streamisErrorCodeHandler;
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisErrorCodeHandler.class);
    private final StreamisErrorCodeManager streamisErrorCodeManager = StreamisErrorCodeManager.getInstance();
    private final ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("streamis-errorcode-handler-%d").build();
    private final long futureTimeOut = ClientConfiguration.FUTURE_TIME_OUT.getValue();

    private final ExecutorService threadPool =
            new ThreadPoolExecutor(
                    5,
                    200,
                    0L,
                    TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<Runnable>(1024),
                    threadFactory,
                    new ThreadPoolExecutor.AbortPolicy());

    public static StreamisErrorCodeHandler getInstance() {
        if (null == streamisErrorCodeHandler) {
            synchronized (StreamisErrorCodeHandler.class) {
                if (null == streamisErrorCodeHandler) {
                    streamisErrorCodeHandler = new StreamisErrorCodeHandler();
                }
            }
        }
        return streamisErrorCodeHandler;
    }
    private StreamisErrorCodeHandler() {
        super();
    }

    @Override
    public List<ErrorCode> handle(String log) {
        Set<ErrorCode> errorCodeSet = new HashSet<>();
        List<LinkisErrorCode> errorCodes = streamisErrorCodeManager.getLinkisErrorCodes();
        Runnable runnable =
                () -> {
                    Arrays.stream(log.split("\n"))
                            .forEach(
                                    singleLog -> {
                                        Option<Tuple2<String, String>> match =
                                                ErrorCodeMatcher.errorMatch(errorCodes, singleLog);
                                        if (match.isDefined()) {
                                            errorCodeSet.add(new LinkisErrorCode(match.get()._1, match.get()._2));
                                        }
                                    });
                };
        Future<?> future = threadPool.submit(runnable);
        try {
            future.get(futureTimeOut, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            LOGGER.error("Failed to parse log in {} ms", futureTimeOut, e);
        }
        return new ArrayList<>(errorCodeSet);
    }

}
