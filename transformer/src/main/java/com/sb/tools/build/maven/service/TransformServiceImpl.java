package com.sb.tools.build.maven.service;

import com.sb.tools.build.maven.utils.TemplateFileMapper;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@Singleton
@Named
public class TransformServiceImpl implements TransformService {

    private final LoadTemplate loadTemplate;
    private final TemplateFileMapper templateFileMapper;

    @Inject
    public TransformServiceImpl(LoadTemplate loadTemplate, TemplateFileMapper templateFileMapper) {
        this.loadTemplate = loadTemplate;
        this.templateFileMapper = templateFileMapper;
    }

    @Override
    public void transform() {
        //TODO Get Templates
        //TODO Load Templates
        //TODO Transform Templates
    }
}
