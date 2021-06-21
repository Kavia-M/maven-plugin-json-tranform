package com.sb.tools.build.maven.service;

import com.google.gson.JsonElement;
import com.sb.tools.build.maven.exceptions.InvalidOverrideException;
import com.sb.tools.build.maven.exceptions.JsonTemplateLoadException;
import com.sb.tools.build.maven.exceptions.TemplateMissingException;
import com.sb.tools.build.maven.exceptions.TransformMavenPluginException;
import com.sb.tools.build.maven.model.JsonTemplateInfo;
import com.sb.tools.build.maven.model.TemplateInfo;
import com.sb.tools.build.maven.utils.TemplateFileMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.isA;

@RunWith(MockitoJUnitRunner.class)
public class JsonTransformServiceTest {
    private LoadTemplate loadTemplate;
    private TemplateFileMapper templateFileMapper;
    private TransformService transformService;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setup() {
        loadTemplate = new LoadJsonTemplate();
        templateFileMapper = new TemplateFileMapper();
        transformService = new JsonTransformService(loadTemplate, templateFileMapper);
    }

    @Test
    public void shouldTransformTemplateBasedOnOverride() throws TransformMavenPluginException {
        List<JsonTemplateInfo> templateInfoList = (List<JsonTemplateInfo>) ((JsonTransformService)transformService)
                .transformTemplates(testJsonTemplate());

        Assert.assertTrue(templateInfoList.size() > 0);

        Path targetDir = Paths.get(System.getProperty("user.dir")).resolve("src/test/resources/target/");
        File expectedResult = new File(String.valueOf(targetDir.resolve("lib/result-iag-template.json")));
        Assert.assertEquals(((JsonElement)loadTemplate.load(expectedResult)), ((JsonElement)templateInfoList.get(0).getResult()));
    }

    @Test
    public void shouldTransformTemplateSuccessfully() throws TemplateMissingException {
        //transformService.transform();
    }

    @Test
    public void shouldTransformPersisTemplateBasedOnOverride() throws TransformMavenPluginException {
        Path targetDir = Paths.get(System.getProperty("user.dir")).resolve("src/test/resources/target/");
        LoadJsonTemplate loadJsonTemplate = new LoadJsonTemplate();
        File templateFile = new File(String.valueOf(targetDir.resolve("lib/dummy-iag-template.json")));
        File overrideFile = new File(String.valueOf(targetDir.resolve("resources/config/dummy-override-iag-template.json")));
        Path resultFile = targetDir.resolve("result/dummy-override-iag-template.json");
        List<JsonTemplateInfo> templateInfoList = (List<JsonTemplateInfo>) ((JsonTransformService)transformService)
                .persistResultTemplates(Arrays.asList(JsonTemplateInfo.JsonTemplateInfoBuilder()
                        .name("Dummy")
                        .templateFile(templateFile)
                        .overrideFile(overrideFile)
                        .resultFile(resultFile)
                        .template(loadJsonTemplate.load(templateFile))
                        .override(loadJsonTemplate.load(overrideFile))
                        .result(loadJsonTemplate.load(new File(String.valueOf(targetDir.resolve("lib/result-iag-template.json")))))
                        .build()));

        //Assert.assertTrue(resultFile.exists());
        //Assert.assertEquals(loadTemplate.load(resultFile), templateInfoList.get(0).getResult());
    }

    @Test
    public void shouldRaiseInvalidOverrideExceptionWhenOverrideInstructionisEmpty() throws TransformMavenPluginException {
        expectedException.expectCause(isA(InvalidOverrideException.class));

        Path targetDir = Paths.get(System.getProperty("user.dir")).resolve("src/test/resources/target/");
        File templateFile = new File(String.valueOf(targetDir.resolve("lib/dummy-iag-template.json")));
        File overrideFile = new File(String.valueOf(targetDir.resolve("resources/config/empty-override-iag-template.json")));
        ((JsonTransformService)transformService).transformTemplates(Arrays.asList(JsonTemplateInfo.JsonTemplateInfoBuilder()
                .name("Dummy")
                .templateFile(templateFile)
                .overrideFile(overrideFile)
                .template((JsonElement) loadTemplate.load(templateFile))
                .override((JsonElement) loadTemplate.load(overrideFile))
                .build()));
    }

    @Test
    public void shouldRaiseInvalidOverrideExceptionWhenOverrideInstructionisIncorrect() throws JsonTemplateLoadException {
        expectedException.expectCause(isA(InvalidOverrideException.class));

        Path targetDir = Paths.get(System.getProperty("user.dir")).resolve("src/test/resources/target/");
        LoadJsonTemplate loadJsonTemplate = new LoadJsonTemplate();
        File templateFile = new File(String.valueOf(targetDir.resolve("lib/dummy-iag-template.json")));
        File overrideFile = new File(String.valueOf(targetDir.resolve("resources/config/error-override-iag-template.json")));
        ((JsonTransformService)transformService).transformTemplates(Arrays.asList(JsonTemplateInfo.JsonTemplateInfoBuilder()
                .name("Dummy")
                .templateFile(templateFile)
                .overrideFile(overrideFile)
                .template(loadJsonTemplate.load(templateFile))
                .override(loadJsonTemplate.load(overrideFile))
                .build()));
    }

    private List<? extends TemplateInfo> testJsonTemplate() throws JsonTemplateLoadException {
        Path targetDir = Paths.get(System.getProperty("user.dir")).resolve("src/test/resources/target/");
        LoadJsonTemplate loadJsonTemplate = new LoadJsonTemplate();
        File templateFile = new File(String.valueOf(targetDir.resolve("lib/dummy-iag-template.json")));
        File overrideFile = new File(String.valueOf(targetDir.resolve("resources/config/dummy-override-iag-template.json")));
        Path resultFile = targetDir.resolve("result/dummy-override-iag-template.json");
        return Arrays.asList(JsonTemplateInfo.JsonTemplateInfoBuilder()
                .name("Dummy")
                .templateFile(templateFile)
                .overrideFile(overrideFile)
                .resultFile(resultFile)
                .template(loadJsonTemplate.load(templateFile))
                .override(loadJsonTemplate.load(overrideFile))
                .build());
    }
}