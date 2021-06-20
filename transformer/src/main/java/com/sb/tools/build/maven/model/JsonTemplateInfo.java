package com.sb.tools.build.maven.model;

import com.google.gson.JsonElement;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.nio.file.Path;

@Getter
@Setter
public class JsonTemplateInfo extends TemplateInfo {
    private JsonElement template;
    private JsonElement override;
    private JsonElement result;

    @Builder(builderMethodName = "JsonTemplateInfoBuilder")
    public JsonTemplateInfo(String name, File templateFile, File overrideFile, Path resultFile,
                            JsonElement template, JsonElement override, JsonElement result, boolean success) {
        super(name, templateFile, overrideFile, resultFile, success);
        this.template = template;
        this.override = override;
        this.result = result;
    }
}
