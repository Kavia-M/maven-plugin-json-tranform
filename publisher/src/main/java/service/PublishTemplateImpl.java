package service;

import exception.PublishMavenPluginException;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Slf4j
@Named
@Singleton
public class PublishTemplateImpl extends PublishTemplate {

    private final PublishService publishService;

    @Inject
    public PublishTemplateImpl(PublishService publishService) {
        this.publishService = publishService;
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            publishService.publish();
        } catch (PublishMavenPluginException e) {
            log.error("Transform Plugin execution failed");
            throw new MojoExecutionException("Transform Plugin execution failed", e);
        }
    }
}
