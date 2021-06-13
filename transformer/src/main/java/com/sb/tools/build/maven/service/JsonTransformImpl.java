package com.sb.tools.build.maven.service;

import com.sb.tools.build.maven.exceptions.TemplateMissingException;
import com.sb.tools.build.maven.utils.TemplateFileIOHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.util.List;

@Slf4j
@Mojo(name="transform", defaultPhase = LifecyclePhase.INSTALL)
public class JsonTransformImpl extends Transform {
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        log.info("***** Transform Plugin Start *****");

        List<File> files = null;
        try {
            files = TemplateFileIOHelper.findTemplateFiles(null);
        } catch (TemplateMissingException e) {
            throw new MojoFailureException("Plugin Execution Failure", e);
        }

        /// TODO transformation here...
    }
}
