package com.sb.tools.build.maven.service;

import com.google.gson.JsonElement;
import com.sb.tools.build.maven.exceptions.TemplateMissingException;
import com.sb.tools.build.maven.model.JsonTemplateInfo;
import com.sb.tools.build.maven.model.TemplateInfo;
import com.sb.tools.build.maven.utils.TemplateFileMapper;
import com.sb.tools.build.maven.utils.ThrowingFunction;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.util.ObjectUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Named
@Singleton
@Slf4j
public class JsonTransformService extends BaseTransformService {

    private final LoadTemplate loadTemplate;
    private final TemplateFileMapper templateFileMapper;

    @Inject
    public JsonTransformService(LoadTemplate loadTemplate, TemplateFileMapper templateFileMapper) {
        this.loadTemplate = loadTemplate;
        this.templateFileMapper = templateFileMapper;
    }

    @Override
    public List<? extends TemplateInfo> getTemplatesForTransformation() throws TemplateMissingException {
        return templateFileMapper.findTemplateFiles();
    }

    @Override
    public List<? extends TemplateInfo> loadTemplates(List<? extends TemplateInfo> templates) {
        return templates.stream()
                .filter(f -> !ObjectUtils.isEmpty(f.getOverrideFile()))
                .map(ThrowingFunction.throwsFunctionWrapper((f) ->
                     JsonTemplateInfo.JsonTemplateInfoBuilder()
                        .name(f.getName())
                        .templateFile(f.getTemplateFile())
                        .overrideFile(f.getOverrideFile())
                        .template((JsonElement)loadTemplate.load(f.getTemplateFile()))
                        .override((JsonElement)loadTemplate.load(f.getOverrideFile()))
                        .build()
                )).collect(Collectors.toList());
    }

    @Override
    public List<? extends TemplateInfo> transformTemplates(List<? extends TemplateInfo> templates) {
        throw new NotImplementedException("Implementation missing");
    }

    @Override
    public List<? extends TemplateInfo> persistTemplates(List<? extends TemplateInfo> templates) {
        throw new NotImplementedException("Implementation missing");
    }
}
