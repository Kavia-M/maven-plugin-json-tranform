package com.sb.tools.build.maven.utils;

import com.sb.tools.build.maven.BaseTransformerTest;
import com.sb.tools.build.maven.exceptions.TemplateMissingException;
import com.sb.tools.build.maven.model.TemplateInfo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.sb.tools.build.maven.utils.TemplateFileMapper.TARGET_OVERRIDE_DIR;
import static com.sb.tools.build.maven.utils.TemplateFileMapper.TARGET_TEMPLATE_DIR;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
@PrepareForTest(TemplateFileMapper.class)
public class TemplateFileMapperTest extends BaseTransformerTest {

    TemplateFileMapper templateFileMapper;

    @Before
    public void setup(){
        templateFileMapper = new TemplateFileMapper();
    }

    @Test
    public void emptyDirShouldDefaultToProjectDirectory() throws Exception {
            PowerMockito.mockStatic(System.class);
            PowerMockito.when(System.getProperty(Mockito.anyString())).thenReturn("C:/maven-plugin-json-transform");

            PowerMockito.mockStatic(Files.class);
            PowerMockito.when(Files.list(Mockito.any())).thenReturn(Arrays.asList(Paths.get("dummy-iag-template.json")).stream());

            Path baseDir = Paths.get(System.getProperty("user.dir"));
            Path targetDir = baseDir.resolve(TARGET_TEMPLATE_DIR);

            TemplateMissingException templateMissingException = Assert.assertThrows(TemplateMissingException.class, () -> {
                templateFileMapper.findTemplateFiles();
            });

            Assert.assertEquals("Error.TemplateNotFound", templateMissingException.getCode());
            Assert.assertEquals(String.format("No template found at %s", targetDir), templateMissingException.getDescription());
    }

    @Test
    public void fileSelectedFromTargetDir() throws Exception, TemplateMissingException {
        Path baseDir = Paths.get(System.getProperty("user.dir"));
        Path targetDir = baseDir.resolve("src/test/resources");

        TemplateInfo templateInfo = TemplateInfo.builder()
                .name("dummy-iag-template.json")
                .templateFile(new File(targetDir.resolve(TARGET_TEMPLATE_DIR).resolve("dummy-iag-template.json").toString()))
                .overrideFile(Optional.of(new File(targetDir.resolve(TARGET_OVERRIDE_DIR).resolve("dummy-override-iag-template.json").toString())))
                .build();

        TemplateInfo refdataTemplateInfo = TemplateInfo.builder()
                .name("dummy-refdata-iag-template.json")
                .templateFile(new File(targetDir.resolve(TARGET_TEMPLATE_DIR).resolve("dummy-refdata-iag-template.json").toString()))
                .overrideFile(Optional.of(new File(targetDir.resolve(TARGET_OVERRIDE_DIR).resolve("dummy-refdata-override-iag-template.json").toString())))
                .build();

        TemplateInfo dummy2TemplateInfo = TemplateInfo.builder()
                .name("dummy2-iag-template.json")
                .templateFile(new File(targetDir.resolve(TARGET_TEMPLATE_DIR).resolve("dummy2-iag-template.json").toString()))
                .overrideFile(Optional.empty())
                .build();

        List<TemplateInfo> actualFiles = templateFileMapper.findTemplateFiles(targetDir.toString());

        Assert.assertTrue(actualFiles.size() == 3);
        Assert.assertEquals(templateInfo, actualFiles.get(0));
        Assert.assertEquals(refdataTemplateInfo, actualFiles.get(1));
        Assert.assertEquals(dummy2TemplateInfo, actualFiles.get(2));
    }
}