package com.sb.tools.build.maven.model;

import com.google.gson.JsonElement;
import lombok.Builder;

import java.io.File;

public class JsonTemplateInfo extends TemplateInfo {
    private JsonElement template;
    private JsonElement override;

    @Builder(builderMethodName = "JsonTemplateInfoBuilder")
    public JsonTemplateInfo(String name, File templateFile, File overrideFile, JsonElement template, JsonElement override) {
        super(name, templateFile, overrideFile);
        this.template = template;
        this.override = override;
    }
}
