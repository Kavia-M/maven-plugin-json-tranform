package com.sb.tools.build.maven.service;

import com.google.gson.JsonElement;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.sb.tools.build.maven.exceptions.InvalidOverrideException;
import com.sb.tools.build.maven.exceptions.TemplateMissingException;
import com.sb.tools.build.maven.model.JsonTemplateInfo;
import com.sb.tools.build.maven.model.TemplateInfo;
import com.sb.tools.build.maven.utils.TemplateFileMapper;
import com.sb.tools.build.maven.utils.ThrowingFunction;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.util.ObjectUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Named
@Singleton
@Slf4j
public class JsonTransformService extends BaseTransformService {

    private final LoadTemplate loadTemplate;
    private final TemplateFileMapper templateFileMapper;

    private final String OVERRIDE_ROOT_OBJECT = "overrideInstructions";

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
                        .resultFile(f.getResultFile())
                        .template((JsonElement)loadTemplate.load(f.getTemplateFile()))
                        .override((JsonElement)loadTemplate.load(f.getOverrideFile()))
                        .build()
                )).collect(Collectors.toList());
    }

    @Override
    public List<? extends TemplateInfo> transformTemplates(List<? extends TemplateInfo> templates) {
        return templates.stream().map(ThrowingFunction.throwsFunctionWrapper(t -> {
            log.info("Transforming template {} based on override {} to result {}",
                    t.getTemplateFile(), t.getOverrideFile(), t.getResultFile());
            JSONArray overrideInstructions;
            try {
                overrideInstructions = JsonPath
                        .read((((JsonTemplateInfo) t).getOverride().toString()), "$." + OVERRIDE_ROOT_OBJECT);
                log.info("{} to be applied {}", "$." + OVERRIDE_ROOT_OBJECT, overrideInstructions);
                if (ObjectUtils.isEmpty(overrideInstructions) || overrideInstructions.size() < 1) {
                    log.error("Error.OverrideInstruction not found for template {}", t.getTemplateFile());
                    throw new InvalidOverrideException("Error.OverrideInstruction",
                            String.format("Error.OverrideInstruction not found for template %s. " +
                                            "Please check contents of override file %s ",
                                    t.getTemplateFile(), t.getOverrideFile()));
                }
            } catch (PathNotFoundException e) {
                throw new InvalidOverrideException("Error.OverrideInstruction",
                        String.format("Error.OverrideInstruction not found for template %s. " +
                                        "Please check contents of override file %s, root object expected to be {} ",
                                t.getTemplateFile(), t.getOverrideFile(), "$." + OVERRIDE_ROOT_OBJECT), e);
            }

            String result = ((JsonTemplateInfo) t).getTemplate().toString();

            for(Object jObj: overrideInstructions) {
                String path = ((LinkedHashMap) jObj).get("path").toString();
                String value = ((LinkedHashMap) jObj).get("value").toString();

                log.info("Applying override path {} with value {} to {}", path, value, result);
                result = JsonPath.parse(result).set(path, value).jsonString();
                log.info("Result {}", result);
            }

            return JsonTemplateInfo.JsonTemplateInfoBuilder()
                    .name(t.getName())
                    .templateFile(t.getTemplateFile())
                    .overrideFile(t.getOverrideFile())
                    .template(((JsonTemplateInfo)t).getTemplate())
                    .override(((JsonTemplateInfo)t).getOverride())
                    .result(((LoadJsonTemplate)loadTemplate).load(result))
                    .resultFile(t.getResultFile())
                    .build();
        })).collect(Collectors.toList());
    }

    @Override
    public List<? extends TemplateInfo> persistResultTemplates(List<? extends TemplateInfo> templates) {
        return templates.stream().map(ThrowingFunction.throwsFunctionWrapper(t -> {
            log.info("Persist Result {} to output path {}", ((JsonTemplateInfo) t).getResult(), t.getResultFile());
            if (!Files.exists(t.getResultFile())) {
                log.info("Result {} does not exist", t.getResultFile());
                Files.createDirectory(t.getResultFile().getParent());
            }
            try (FileWriter fileWriter = new FileWriter(t.getResultFile().toString())) {
                fileWriter.write(((JsonTemplateInfo) t).getResult().toString());
            } catch (IOException e) {
                log.error("Error.IOException could not write result for template {}", ((JsonTemplateInfo) t).getTemplateFile());
                throw new InvalidOverrideException("Error.IOException",
                        String.format("Error.IOException for template %s. " +
                                        "Please check result file %s ",
                                t.getTemplateFile(), t.getResultFile(), e));
            }
            return JsonTemplateInfo.JsonTemplateInfoBuilder()
                    .name(t.getName())
                    .templateFile(t.getTemplateFile())
                    .overrideFile(t.getOverrideFile())
                    .template(((JsonTemplateInfo)t).getTemplate())
                    .override(((JsonTemplateInfo)t).getOverride())
                    .result(((JsonTemplateInfo)t).getResult())
                    .resultFile(t.getResultFile())
                    .success(true)
                    .build();
        })).collect(Collectors.toList());
    }
}
