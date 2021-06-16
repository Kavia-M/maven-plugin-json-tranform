package com.sb.tools.build.maven.service;

import com.sb.tools.build.maven.exceptions.TemplateMissingException;
import com.sb.tools.build.maven.model.TemplateInfo;
import com.sb.tools.build.maven.utils.TemplateFileMapper;
import org.apache.commons.lang3.NotImplementedException;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;

@Named
@Singleton
public class JsonTransformService extends BaseTransformService {

    private final LoadTemplate loadTemplate;
    private final TemplateFileMapper templateFileMapper;

    @Inject
    public JsonTransformService(LoadTemplate loadTemplate, TemplateFileMapper templateFileMapper) {
        this.loadTemplate = loadTemplate;
        this.templateFileMapper = templateFileMapper;
    }

    @Override
    public List<TemplateInfo> getTemplatesForTransformation() throws TemplateMissingException {
        throw new NotImplementedException("Implementation missing");
    }

    @Override
    public List<TemplateInfo> loadTemplates(List<TemplateInfo> templates) {
        throw new NotImplementedException("Implementation missing");
    }

    @Override
    public List<TemplateInfo> transformTemplates(List<TemplateInfo> templates) {
        throw new NotImplementedException("Implementation missing");
    }

    @Override
    public List<TemplateInfo> persistTemplates(List<TemplateInfo> templates) {
        throw new NotImplementedException("Implementation missing");
    }
}
