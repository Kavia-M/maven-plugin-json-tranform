package com.sb.tools.build.maven.service;

import com.sb.tools.build.maven.exceptions.JsonTemplateLoadException;
import com.sb.tools.build.maven.exceptions.TransformMavenPluginException;

import java.io.File;

@FunctionalInterface
public interface LoadTemplate {
    Object load(File file) throws TransformMavenPluginException, JsonTemplateLoadException;
}
