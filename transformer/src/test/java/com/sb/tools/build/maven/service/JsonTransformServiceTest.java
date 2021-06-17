package com.sb.tools.build.maven.service;

import com.sb.tools.build.maven.exceptions.TemplateMissingException;
import com.sb.tools.build.maven.model.JsonTemplateInfo;
import com.sb.tools.build.maven.model.TemplateInfo;
import com.sb.tools.build.maven.utils.TemplateFileMapper;
import org.apache.commons.lang3.NotImplementedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class JsonTransformServiceTest {
    private LoadTemplate loadTemplate;
    private TemplateFileMapper templateFileMapper;
    private TransformService transformService;

    @Before
    public void setup() {
        loadTemplate = Mockito.mock(LoadTemplate.class);
        templateFileMapper = Mockito.mock(TemplateFileMapper.class);
        transformService = new JsonTransformService(loadTemplate, templateFileMapper);
    }

    @Test (expected = NotImplementedException.class)
    public void testGetTemplatesForTransformation() throws TemplateMissingException {
        Mockito.when((templateFileMapper)
                .findTemplateFiles())
                .thenReturn((List<TemplateInfo>) testJsonTemplate());

        transformService.transform();

        Mockito.verify(((JsonTransformService)transformService).getTemplatesForTransformation(), Mockito.times(2));
    }

    private List<? extends TemplateInfo> testJsonTemplate() {
        return Arrays.asList(JsonTemplateInfo.JsonTemplateInfoBuilder()
                .name("Dummy")
                .templateFile(new File("dummy/dummypath"))
                .overrideFile(new File("dummy/overridepath"))
                .override(null)
                .template(null)
                .build());
    }
}