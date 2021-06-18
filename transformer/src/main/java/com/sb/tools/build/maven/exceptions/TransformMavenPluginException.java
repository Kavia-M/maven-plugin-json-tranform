package com.sb.tools.build.maven.exceptions;

import lombok.Getter;

@Getter
public class TransformMavenPluginException extends Throwable {
    public final String code;
    public final String description;
    public final Throwable cause;

    public TransformMavenPluginException(String code, String description, Throwable cause){
        super(code + ":" + description, cause);
        this.code = code;
        this.description = description;
        this.cause = cause;
    }
}
