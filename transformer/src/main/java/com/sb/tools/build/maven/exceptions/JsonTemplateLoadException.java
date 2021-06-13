package com.sb.tools.build.maven.exceptions;

public class JsonTemplateLoadException extends TransformMavenPluginException {
    public JsonTemplateLoadException(String code, String description) {
        super(code, description);
    }
}
