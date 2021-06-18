package com.sb.tools.build.maven.utils;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Throwable> {
    void consume(T in) throws E;
    static <T> Consumer<T> throwsConsumerWrapper (
            ThrowingConsumer<T, Throwable> throwingConsumer
    ) {
        return f -> {
            try {
                throwingConsumer.consume(f);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        };
    }
}
