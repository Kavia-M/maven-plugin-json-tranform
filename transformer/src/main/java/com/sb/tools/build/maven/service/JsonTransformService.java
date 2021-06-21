package com.sb.tools.build.maven.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.PathNotFoundException;
import com.sb.tools.build.maven.exceptions.InvalidOverrideException;
import com.sb.tools.build.maven.exceptions.TemplateMissingException;
import com.sb.tools.build.maven.exceptions.TransformMavenPluginException;
import com.sb.tools.build.maven.model.JsonTemplateInfo;
import com.sb.tools.build.maven.model.TemplateInfo;
import com.sb.tools.build.maven.utils.TemplateFileMapper;
import com.sb.tools.build.maven.utils.ThrowingFunction;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
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
    public JsonElement transformTemplate(TemplateInfo t) throws InvalidOverrideException {
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

            DocumentContext context = JsonPath.parse(((JsonTemplateInfo) t).getTemplate().toString());

            for(Object obj: overrideInstructions) {
                if (obj.getClass() != LinkedHashMap.class) {
                    log.error("Error.OverrideInstruction malformed " +
                                    "Please check contents of override file {}, root object expected to be {} ",
                            t.getOverrideFile(), "$." + OVERRIDE_ROOT_OBJECT);
                    throw new InvalidOverrideException("Error.OverrideInstruction", String.format("Error.OverrideInstruction malformed " +
                                    "Please check contents of override file %s, root object expected to be %s ",
                            t.getOverrideFile(), "$." + OVERRIDE_ROOT_OBJECT));
                }

                LinkedHashMap jObj = ((LinkedHashMap) obj);
                if (jObj.size() < 2) {
                    log.error("Error.OverrideInstruction malformed instruction {}. " +
                                    "Please check contents of override file {}, root object expected to be {} ",
                            jObj, t.getOverrideFile(), "$." + OVERRIDE_ROOT_OBJECT);
                    throw new InvalidOverrideException("Error.OverrideInstruction",
                            String.format("Error.OverrideInstruction malformed instruction %s. " +
                                            "Please check contents of override file %s, root object expected to be %s ",
                                    jObj, t.getOverrideFile(), "$." + OVERRIDE_ROOT_OBJECT));
                }
                String path = jObj.get("path").toString();
                String value = jObj.get("value").toString();
                Object overrideInstructionSource = jObj.get("source");
                Object overrideInstructionType = jObj.get("type");

                if (!ObjectUtils.isEmpty(overrideInstructionSource)) {
                    handleOverrideInstructionSource(context, jObj, overrideInstructionSource, overrideInstructionType, path, value);
                } else if (!ObjectUtils.isEmpty(overrideInstructionType)) {
                    log.warn("No source information for override path {} with value {}", path, value);
                    handleOverrideInstructionType(context, overrideInstructionType, path, value);
                } else {
                    log.warn("No source information for override path {} with value {}", path, value);
                    log.warn("No type information for override path {} with value {}", path, value);
                    handlePlainValues(context, path, value);
                }
            }

            String result = context.jsonString();
            log.info("Result {}", result);
            return ((LoadJsonTemplate)loadTemplate).load(result);
    }

    private void handleOverrideInstructionType(DocumentContext context, Object overrideInstructionType,
                                                          String path, String value) {
        if (!StringUtils.isEmpty(overrideInstructionType) && overrideInstructionType.equals("object")) {
            log.info("Applying override path {} with object value {}", path, value);
            context.set(JsonPath.compile(path), JsonPath.parse(value).json());
        } else if (!StringUtils.isEmpty(overrideInstructionType) && overrideInstructionType.equals("list")) {
            log.info("Applying override path {} with object value {}", path, value);
            context.add(JsonPath.compile(path), new Gson().fromJson(value,
                    new TypeToken<HashMap<String, Object>>() {}.getType()));
        }
    }

    private void handleOverrideInstructionSource(DocumentContext context, LinkedHashMap jObj,
                                                            Object overrideInstructionSource,
                                                            Object overrideInstructionType,
                                                            String path, String value) throws InvalidOverrideException {
        String source = String.valueOf(Paths.get(System.getProperty("user.dir")).resolve(value));
        try {
            if (!ObjectUtils.isEmpty(overrideInstructionType)) {
                handleOverrideInstructionType(context, overrideInstructionType, path,
                        loadTemplate.load(new File(source)).toString());
            } else {
                log.warn("No type information for override path {} with value {}", path, value);
                handlePlainValues(context, path,
                        loadTemplate.load(new File(source)).toString());
            }
        } catch (TransformMavenPluginException e) {
            log.error("OverrideInstruction Source {} does not exist", source);
            throw new InvalidOverrideException("Error.OverrideInstruction",
                    String.format("Error.OverrideInstruction malformed instruction %s. " +
                                    "Override Instruction source %s, does not exist",
                            jObj, overrideInstructionSource.toString()), e);
        }
    }

    private void handlePlainValues(DocumentContext context, String path, String value) {
        log.info("Applying override path {} with value {}", path, value);
        context.set(JsonPath.compile(path), value);
    }

    @Override
    public boolean persistResultTemplate(TemplateInfo t, JsonElement result) throws InvalidOverrideException {
            log.info("Persist Result {} to output path {}", ((JsonTemplateInfo) t).getResult(), t.getResultFile());
            if (!Files.exists(t.getResultFile().getParent())) {
                log.info("Result {} does not exist", t.getResultFile());
                try {
                    Files.createDirectory(t.getResultFile().getParent());
                } catch (IOException e) {
                    log.error("Error.IOException could not create directory for template {}", t.getTemplateFile());
                    throw new InvalidOverrideException("Error.IOException", String.format("Error.IOException for template %s. " +
                                            "Please check result file %s ", t.getTemplateFile(), t.getResultFile()), e);
                }
            }
            try (FileWriter fileWriter = new FileWriter(t.getResultFile().toString())) {
                fileWriter.write(result.toString());
                log.info("\r\n<----- Transformation complete \r\n found Template {} \r\n Override {} \r\n Result {} \r\n ----->",
                        t.getTemplateFile(), t.getOverrideFile(), t.getResultFile());
            } catch (IOException e) {
                log.error("Error.IOException could not write result for template {}", ((JsonTemplateInfo) t).getTemplateFile());
                throw new InvalidOverrideException("Error.IOException",
                        String.format("Error.IOException for template %s. " +
                                        "Please check result file %s ",
                                t.getTemplateFile(), t.getResultFile()), e);
            }
            return true;
    }
}
