package com.sb.tools.build.maven.utils;

import com.sb.tools.build.maven.exceptions.TransformMavenPluginException;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends TransformMavenPluginException> {
    R apply(T in) throws E;
    static <T, R> Function<T, R> throwsFunctionWrapper (
            ThrowingFunction<T, R, TransformMavenPluginException> throwingFunction
    ) {
        return f -> {
            try {
                return throwingFunction.apply(f);
            } catch (TransformMavenPluginException e) {
                throw new RuntimeException (e);
            }
        };
    }
}

