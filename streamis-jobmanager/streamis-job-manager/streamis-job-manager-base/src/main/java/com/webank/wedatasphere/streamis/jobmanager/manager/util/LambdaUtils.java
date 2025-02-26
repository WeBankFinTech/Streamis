package com.webank.wedatasphere.streamis.jobmanager.manager.util;

import org.apache.linkis.common.exception.WarnException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface LambdaUtils {

    static final Logger LOGGER = LoggerFactory.getLogger(LambdaUtils.class);

    static <Out, E extends WarnException> Out supply(Supplier<Out> supplier,
                                                     java.util.function.Function<Exception, E> createException) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw createException.apply(e);
        }
    }

    static <In, E extends WarnException> java.util.function.Consumer<In> consume(Consumer<In> consumer,
                                                                                 java.util.function.Function<Exception, E> createException) {
        return in -> {
            try {
                consumer.accept(in);
            } catch (Exception e) {
                throw createException.apply(e);
            }
        };
    }

    static <In, Out, E extends WarnException> java.util.function.Function<In, Out> apply(Function<In, Out> function,
                                                                                         java.util.function.Function<Exception, E> createException) {
        return in -> {
            try {
                return function.apply(in);
            } catch (Exception e) {
                throw createException.apply(e);
            }
        };
    }

    static <In> java.util.function.Consumer<In> consumeAndWarn(Consumer<In> consumer) {
        return in -> {
            try {
                consumer.accept(in);
            } catch (Exception e) {
                LOGGER.warn("运行失败，原因：", e);
            }
        };
    }

    interface Supplier<T> {

        T get() throws Exception;

    }

    interface Consumer<T> {

        void accept(T t) throws Exception;

    }

    interface Function<In, Out> {

        Out apply(In in) throws Exception;

    }

}
