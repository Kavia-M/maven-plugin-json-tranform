package com.sb.tools.build.maven.utils;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Throwable> {
    R apply(T in) throws E;
    static <T, R> Function<T, R> throwsFunctionWrapper (
            ThrowingFunction<T, R, Throwable> throwingFunction
    ) {
        return f -> {
            try {
                return throwingFunction.apply(f);
            } catch (Throwable e) {
                throw new RuntimeException (e);
            }
        };
    }
}

