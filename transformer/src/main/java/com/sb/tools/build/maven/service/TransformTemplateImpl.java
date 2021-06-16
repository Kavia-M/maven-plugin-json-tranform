package com.sb.tools.build.maven.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Slf4j
@Mojo(name="transform", defaultPhase = LifecyclePhase.INSTALL)
public class TransformTemplateImpl extends TransformTemplate {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        log.info("***** Transform Plugin Start *****");

        /*try {
            List<File> templateFiles = TemplateFileIOHelper.findTemplateFiles(null);
            List<String> templates = templateFiles.stream()
                    .map(ThrowingFunction.throwsFunctionWrapper(f -> (String)loadTemplate.load(f)))
                    .collect(Collectors.toList());

            log.info("{} template files found", templates.size());
            templates.forEach(f -> log.info("Template Json: {}", templates));

            List<File> overrideFiles = TemplateFileIOHelper.findTemplateFiles(null);
            overrideFiles.stream()
                    .map(ThrowingFunction.throwsFunctionWrapper(f -> loadTemplate.load(f)))
                    .collect(Collectors.toList());

        } catch (TemplateMissingException e) {
            throw new MojoFailureException("Plugin Execution Failure", e);
        }*/
    }
}
