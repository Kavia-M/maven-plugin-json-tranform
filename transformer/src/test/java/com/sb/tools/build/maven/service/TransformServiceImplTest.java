package com.sb.tools.build.maven.service;

import com.sb.tools.build.maven.utils.TemplateFileMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TransformServiceImplTest {

    private LoadTemplate loadTemplate;
    private TemplateFileMapper templateFileMapper;
    private TransformService transformService;

    @Before
    public void setup() {
        loadTemplate = Mockito.mock(LoadTemplate.class);
        templateFileMapper = Mockito.mock(TemplateFileMapper.class);

        transformService = new TransformServiceImpl(loadTemplate, templateFileMapper);
    }

    @Test
    public void executeTransformSuccessfully() {
        transformService.transform();
        Assert.assertTrue(true);
    }
}