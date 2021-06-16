package com.sb.tools.build.maven.service;

import com.sb.tools.build.maven.exceptions.TemplateMissingException;
import com.sb.tools.build.maven.model.TemplateInfo;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

@Singleton
@Named
public abstract class BaseTransformService implements TransformService {
    @Override
    public void transform() throws TemplateMissingException {
        persistTemplates(transformTemplates(loadTemplates(getTemplatesForTransformation())));
    }

    public abstract List<TemplateInfo> getTemplatesForTransformation() throws TemplateMissingException;
    public abstract List<TemplateInfo> loadTemplates(List<TemplateInfo> templates);
    public abstract List<TemplateInfo> transformTemplates(List<TemplateInfo> templates);
    public abstract List<TemplateInfo> persistTemplates(List<TemplateInfo> templates);
}
