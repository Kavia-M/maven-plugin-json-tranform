package com.sb.tools.build.maven.utils;

import com.sb.tools.build.maven.BaseTransformerTest;
import com.sb.tools.build.maven.exceptions.TemplateMissingException;
import org.junit.Assert;
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
import java.util.stream.Collectors;

import static com.sb.tools.build.maven.utils.TemplateFileMapper.TARGET_TEMPLATE_DIR;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
@PrepareForTest(TemplateFileMapper.class)
public class TemplateFileMapperTest extends BaseTransformerTest {

    @Test
    public void emptyDirShouldDefaultToProjectDirectory() throws Exception {
            PowerMockito.mockStatic(System.class);
            PowerMockito.when(System.getProperty(Mockito.anyString())).thenReturn("C:/maven-plugin-json-transform");

            PowerMockito.mockStatic(Files.class);
            PowerMockito.when(Files.list(Mockito.any())).thenReturn(Arrays.asList(Paths.get("dummy-iag-template.json")).stream());

            Path baseDir = Paths.get(System.getProperty("user.dir"));
            Path targetDir = baseDir.resolve(TARGET_TEMPLATE_DIR);

            TemplateMissingException templateMissingException = Assert.assertThrows(TemplateMissingException.class, () -> {
                TemplateFileMapper.findTemplateFiles(null);
            });

            Assert.assertEquals("Error.TemplateNotFound", templateMissingException.getCode());
            Assert.assertEquals(String.format("No template found at %s", targetDir), templateMissingException.getDescription());
    }

    @Test
    public void fileSelectedFromTargetDir() throws Exception, TemplateMissingException {
        Path baseDir = Paths.get(System.getProperty("user.dir"));
        Path targetDir = baseDir.resolve("src/test/resources");

        List<File> expectedFiles = Files.list(targetDir.resolve(TARGET_TEMPLATE_DIR))
                .map(Path::toFile)
                .filter(f -> f.isFile() && f.getName().endsWith("-iag-template.json"))
                .collect(Collectors.toList());

        List<File> actualFiles = TemplateFileMapper.findTemplateFiles(targetDir.toString());

        Assert.assertEquals(expectedFiles, actualFiles);
    }
}