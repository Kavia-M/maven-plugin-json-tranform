package com.sb.tools.build.maven.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.sb.tools.build.maven.exceptions.JsonTemplateLoadException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

@Slf4j
public class LoadJsonTemplate implements LoadTemplate {

    @Override
    public JsonElement load(File file) throws JsonTemplateLoadException {
        if (ObjectUtils.isEmpty(file)) {
            log.error("File object is empty aborting....");
            throw new JsonTemplateLoadException("Error.FileNotFoundException", "File object is empty");
        }

        try (FileReader fileReader = new FileReader(file)) {
            log.info("Successfully loaded file {}", file.getName());
            return JsonParser.parseReader(new JsonReader(fileReader));
        } catch (FileNotFoundException e) {
            log.error("Error Loading the file: {}", e.getMessage());
            throw new JsonTemplateLoadException("Error.FileNotFoundException", e.getMessage());
        } catch (IOException e) {
            log.error("Error Loading the file: {}", e.getMessage());
            throw new JsonTemplateLoadException("Error.IOException", e.getMessage());
        }
    }
}
