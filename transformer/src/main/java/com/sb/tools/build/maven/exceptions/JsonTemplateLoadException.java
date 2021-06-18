package com.sb.tools.build.maven.exceptions;

public class JsonTemplateLoadException extends TransformMavenPluginException {
    public JsonTemplateLoadException(String code, String description, Throwable cause) {
        super(code, description, cause);
    }

    public JsonTemplateLoadException(String code, String description) {
        this(code, description, null);
    }
}
