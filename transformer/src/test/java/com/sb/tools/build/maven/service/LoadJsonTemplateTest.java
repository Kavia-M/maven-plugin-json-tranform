package com.sb.tools.build.maven.service;

import com.google.gson.JsonElement;
import com.sb.tools.build.maven.BaseTransformerTest;
import com.sb.tools.build.maven.exceptions.JsonTemplateLoadException;
import com.sb.tools.build.maven.exceptions.TransformMavenPluginException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static com.sb.tools.build.maven.utils.TemplateFileMapper.TARGET_TEMPLATE_DIR;

@RunWith(MockitoJUnitRunner.class)
public class LoadJsonTemplateTest extends BaseTransformerTest {
    LoadTemplate loadTemplate = new LoadJsonTemplate();

    @Test (expected = JsonTemplateLoadException.class)
    public void throwErrorIfInputFileIsNull() throws TransformMavenPluginException {
        loadTemplate.load(null);
    }

    @Test (expected = JsonTemplateLoadException.class)
    public void throwFileNotFoundErrorIfInputFileDoesNotExist() throws TransformMavenPluginException {
        loadTemplate.load(new File("dummy.json"));
    }

    @Test
    public void loadValidJsonFile() throws IOException, TransformMavenPluginException {
        Path baseDir = Paths.get(System.getProperty("user.dir"));
        Path targetDir = baseDir.resolve("src/test/resources");

        List<File> expectedFiles = Files.list(targetDir.resolve(TARGET_TEMPLATE_DIR))
                .map(Path::toFile)
                .filter(f -> f.isFile() && f.getName().endsWith("-iag-template.json"))
                .collect(Collectors.toList());

        Assert.assertTrue(expectedFiles.size() == 1);
        JsonElement actualResult = (JsonElement)loadTemplate.load(expectedFiles.get(0));
        Assert.assertTrue(actualResult != null);
    }
}