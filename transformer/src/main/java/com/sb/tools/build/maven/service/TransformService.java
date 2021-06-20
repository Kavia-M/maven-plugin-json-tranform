package com.sb.tools.build.maven.service;

import com.sb.tools.build.maven.exceptions.TemplateMissingException;

@FunctionalInterface
public interface TransformService {
    void transform() throws TemplateMissingException;
}
