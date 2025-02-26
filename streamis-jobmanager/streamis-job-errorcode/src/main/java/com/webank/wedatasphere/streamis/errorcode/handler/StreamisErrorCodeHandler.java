package com.webank.wedatasphere.streamis.errorcode.handler;

import com.webank.wedatasphere.streamis.errorcode.entity.StreamErrorCode;
import com.webank.wedatasphere.streamis.errorcode.manager.cache.StreamisErrorCodeCache;
import com.webank.wedatasphere.streamis.errorcode.utils.StreamisErrorCodeMatcher;
import org.apache.linkis.common.utils.Utils;
import org.apache.linkis.errorcode.client.ClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Option;
import scala.Tuple3;

import java.util.*;
import java.util.concurrent.*;

public class StreamisErrorCodeHandler {

    private static StreamisErrorCodeHandler streamisErrorCodeHandler;
    private static final Logger LOGGER = LoggerFactory.getLogger(StreamisErrorCodeHandler.class);
    private final long futureTimeOut = ClientConfiguration.FUTURE_TIME_OUT.getValue();


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

    public List<StreamErrorCode> handle(String log) {
        Set<StreamErrorCode> errorCodeSet = new HashSet<>();
        List<StreamErrorCode> streamErrorCodes = StreamisErrorCodeCache.get("data");
        Runnable runnable =
                () -> {
                    Arrays.stream(log.split("\n"))
                            .forEach(
                                    singleLog -> {
                                        Option<Tuple3<String, String,String>> match =
                                                StreamisErrorCodeMatcher.errorMatch(streamErrorCodes, singleLog);
                                        if (match.isDefined()) {
                                            errorCodeSet.add(new StreamErrorCode(match.get()._1(), match.get()._2(),match.get()._3()));
                                        }
                                    });
                };
        Future<?> future = Utils.defaultScheduler().submit(runnable);
        try {
            future.get(futureTimeOut, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            LOGGER.error("Failed to parse log in {} ms", futureTimeOut, e);
        }
        return new ArrayList<>(errorCodeSet);
    }

}
