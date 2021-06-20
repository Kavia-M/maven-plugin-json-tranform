package com.sb.tools.build.maven.service;

import com.google.gson.JsonElement;
import com.sb.tools.build.maven.BaseTransformerTest;
import com.sb.tools.build.maven.exceptions.TransformMavenPluginException;
import com.sb.tools.build.maven.utils.TemplateFileMapper;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.IOException;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("jdk.internal.reflect.*")
@PrepareForTest(TransformTemplateImpl.class)
public class TransformTemplateImplTest extends BaseTransformerTest {

    LoadTemplate loadTemplate = Mockito.mock(LoadJsonTemplate.class);
    JsonElement templateJson;
    TransformTemplate transformTemplate;


    @Before
    public void setup() throws IOException {
/*        transformTemplate = new TransformTemplateImpl();
        Path baseDir = Paths.get(System.getProperty("user.dir"));
        Path targetDir = baseDir.resolve("src/test/resources");

        List<File> expectedFiles = Files.list(targetDir.resolve(TARGET_TEMPLATE_DIR))
                .map(Path::toFile)
                .filter(f -> f.isFile() && f.getName().endsWith("-iag-template.json"))
                .collect(Collectors.toList());

        Assert.assertTrue(expectedFiles.size() > 0);
        File file = expectedFiles.get(0);
        try (FileReader fileReader = new FileReader(file)) {
            templateJson = JsonParser.parseReader(new JsonReader(fileReader));
        }*/
    }

    @Test
    public void executeMavenPluginSuccessfully() throws TransformMavenPluginException, MojoFailureException, MojoExecutionException {
        PowerMockito.mockStatic(TemplateFileMapper.class);
        /*PowerMockito.when(TemplateFileIOHelper.findTemplateFiles(Mockito.any()))
                .thenReturn(Arrays.asList(new File("dummy.json")));*/

        //Mockito.when(loadTemplate.load(Mockito.any())).thenReturn(templateJson);
        //transformTemplate.execute();
        //Mockito.verify(loadTemplate, Mockito.times(2)).load(Mockito.any());
    }
}