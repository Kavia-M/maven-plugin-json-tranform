package com.sb.tools.build.maven.model;

import com.google.gson.JsonElement;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JsonTemplateInfo extends TemplateInfo {
    private JsonElement template;
    private JsonElement override;
}
