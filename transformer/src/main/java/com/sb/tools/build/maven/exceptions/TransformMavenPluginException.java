package com.sb.tools.build.maven.exceptions;

import lombok.Getter;

@Getter
public class TransformMavenPluginException extends Throwable {
    public final String code;
    public final String description;

    public TransformMavenPluginException(String code, String description){
        this.code = code;
        this.description = description;
    }
}
