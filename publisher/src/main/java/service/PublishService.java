package service;

import exception.PublishMavenPluginException;

@FunctionalInterface
public interface PublishService {
    void publish() throws PublishMavenPluginException;
}
