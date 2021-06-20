package com.sb.tools.build.maven.exceptions;

public class TemplateMissingException extends TransformMavenPluginException {
    public TemplateMissingException(String code, String description, Throwable cause) {
        super(code, description, cause);
    }

    public TemplateMissingException(String code, String description) {
        super(code, description, null);
    }
}
