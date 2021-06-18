package com.sb.tools.build.maven.service;

import com.sb.tools.build.maven.exceptions.TemplateMissingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Slf4j
@Singleton
@Named
@Mojo(name="transform", defaultPhase = LifecyclePhase.INSTALL)
public class TransformTemplateImpl extends TransformTemplate {

    private final TransformService transformService;

    @Inject
    public TransformTemplateImpl(TransformService transformService) {
        this.transformService = transformService;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        log.info("***** Transform Plugin Start *****");

        try {
            transformService.transform();
        } catch (TemplateMissingException e) {
            log.error("Transform Plugin execution failed");
            throw new MojoExecutionException("Transform Plugin execution failed", e);
        }
    }
}
