package com.sb.tools.build.maven.model;

import lombok.Builder;
import lombok.Data;

import java.io.File;
import java.util.Optional;

@Data
@Builder
public class TemplateInfo {
    private String name;
    private File templateFile;
    private Optional<File> overrideFile;
}
