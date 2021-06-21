package com.sb.tools.build.maven.service;

import com.google.common.annotations.VisibleForTesting;
import com.google.gson.JsonElement;
import com.sb.tools.build.maven.exceptions.InvalidOverrideException;
import com.sb.tools.build.maven.exceptions.TemplateMissingException;
import com.sb.tools.build.maven.model.JsonTemplateInfo;
import com.sb.tools.build.maven.model.TemplateInfo;
import com.sb.tools.build.maven.utils.ThrowingFunction;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.List;
import java.util.stream.Collectors;

@Singleton
@Named
public abstract class BaseTransformService implements TransformService {

    @Override
    public void transform() throws TemplateMissingException {
        transformTemplates(loadTemplates(getTemplatesForTransformation()));
    }

    @VisibleForTesting
    protected List<? extends TemplateInfo> transformTemplates(List<? extends TemplateInfo> templates) {
        return templates.stream().map(ThrowingFunction.throwsFunctionWrapper(t -> {
            JsonElement result = transformTemplate(t);
            return JsonTemplateInfo.JsonTemplateInfoBuilder()
                    .name(t.getName())
                    .templateFile(t.getTemplateFile())
                    .overrideFile(t.getOverrideFile())
                    .template(((JsonTemplateInfo)t).getTemplate())
                    .override(((JsonTemplateInfo)t).getOverride())
                    .result(result)
                    .resultFile(t.getResultFile())
                    .success(persistResultTemplate(t, result))
                    .build();
        })).collect(Collectors.toList());
    }

    public abstract List<? extends TemplateInfo> getTemplatesForTransformation() throws TemplateMissingException;
    public abstract List<? extends TemplateInfo> loadTemplates(List<? extends TemplateInfo> templates);
    public abstract JsonElement transformTemplate(TemplateInfo template) throws InvalidOverrideException;
    public abstract boolean persistResultTemplate(TemplateInfo template, JsonElement result) throws InvalidOverrideException;
}
