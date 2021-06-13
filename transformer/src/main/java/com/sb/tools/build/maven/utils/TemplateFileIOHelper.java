package com.sb.tools.build.maven.utils;

import com.sb.tools.build.maven.exceptions.TemplateMissingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class TemplateFileIOHelper {

    public static String TARGET_DIR = "target/lib/";

    public static List<File> findTemplateFiles(String dir) throws TemplateMissingException {
        if (StringUtils.isEmpty(dir)) {
            dir = System.getProperty("user.dir");
            log.info("No custom directory provided defaulting to {}", dir);
        }

        Path targetDir = Paths.get(dir).resolve(TARGET_DIR);
        log.info("Template target {}", targetDir);

        try {
            List<File> files = Files.list(targetDir)
                    .map(Path::toFile)
                    .filter(f -> f.isFile() && f.getName().endsWith("-iag-template.json"))
                    .collect(Collectors.toList());

            if (files.size() == 0)
                throw new TemplateMissingException("Error.TemplateNotFound", String.format("No template found at %s", targetDir));

            files.forEach(f -> log.info("found file {}", f));
            return files;
        } catch (IOException e) {
            log.error("Error while reading the directory {} due to following error {}", targetDir, e.getMessage());
            throw new TemplateMissingException("Error.IOException", String.format("Error while reading the directory %s due to following error %s", targetDir, e.getMessage()));
        }
    }
}
