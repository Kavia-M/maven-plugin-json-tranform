package com.sb.tools.build.maven.exceptions;

public class InvalidOverrideException extends TransformMavenPluginException {
    public InvalidOverrideException(String code, String description, Throwable cause) {
        super(code, description, cause);
    }

    public InvalidOverrideException(String code, String description) {
        this(code, description, null);
    }
}
