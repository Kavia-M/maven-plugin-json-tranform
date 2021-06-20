package com.sb.tools.build.maven.utils;

import com.sb.tools.build.maven.exceptions.TemplateMissingException;
import com.sb.tools.build.maven.model.TemplateInfo;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Named;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Named
@Singleton
public class TemplateFileMapper {

    public static String TARGET_TEMPLATE_DIR = "target/lib/";
    public static String TARGET_OVERRIDE_DIR = "src/main/resources/config";
    public static String TARGET_RESULT_DIR = "target/result/";

    public List<TemplateInfo> findTemplateFiles(String dir, String templatePattern, String overrideTemplatePattern) throws TemplateMissingException {
        return findFiles(Paths.get(dir), templatePattern, overrideTemplatePattern);
    }

    public List<TemplateInfo> findTemplateFiles() throws TemplateMissingException {
        log.info("No template directory provided defaulting to {}", System.getProperty("user.dir"));
        return findTemplateFiles(System.getProperty("user.dir"));
    }

    public List<TemplateInfo> findTemplateFiles(String dir) throws TemplateMissingException {
        log.info("No template pattern provided defaulting to {}", "-iag-template.json");
        return findTemplateFiles(dir, "-iag-template.json");
    }

    public List<TemplateInfo> findTemplateFiles(String dir, String templatePattern) throws TemplateMissingException {
        log.info("No override pattern provided defaulting to {}", "-iag-template.json");
        return findTemplateFiles(dir, templatePattern, "-override-iag-template.json");
    }

    private List<TemplateInfo> findFiles(Path targetPath, String templatePattern, String overridePattern) throws TemplateMissingException {
        Path templatePath = targetPath.resolve(TARGET_TEMPLATE_DIR);
        log.info("Processing target {}", targetPath);

        try {
            List<TemplateInfo> files = Files.list(templatePath)
                    .map(Path::toFile)
                    .filter(f -> f.isFile() && f.getName().endsWith(templatePattern))
                    .map(ThrowingFunction.throwsFunctionWrapper(f -> {
                        String templateToken = f.getName()
                                .substring(0, f.getName().length() - templatePattern.length());
                        Optional<File> overrideFile = Optional.empty();
                        try {
                            overrideFile = Files.list(targetPath.resolve(TARGET_OVERRIDE_DIR))
                                    .map(Path::toFile)
                                    .filter(of -> of.isFile() && of.getName().endsWith(templateToken + overridePattern))
                                    .findFirst();

                        } catch (IOException e) {
                            log.error("IOException in processing template {} and override {}",
                                    f.getName(), templateToken + TARGET_OVERRIDE_DIR);
                            throw new TemplateMissingException("Error.IOExeption",
                                    String.format("IOException in processing template %s and override %s",
                                    f.getName(), targetPath.resolve(TARGET_OVERRIDE_DIR).resolve(templateToken + overridePattern)), e);
                        }

                        return TemplateInfo.builder()
                                .name(f.getName())
                                .templateFile(f)
                                .overrideFile(overrideFile.isPresent() ? overrideFile.get() : null)
                                .resultFile(targetPath.resolve(TARGET_RESULT_DIR).resolve(f.getName()))
                                .build();
                    }))
                    .collect(Collectors.toList());

            if (files.size() == 0)
                throw new TemplateMissingException("Error.TemplateNotFound", String.format("No template found at %s", templatePath));

            files.forEach(f -> log.info("\r\n<----- \r\n Found Template {} \r\n Override {} \r\n Result {} \r\n ----->",
                    f.getTemplateFile(), f.getOverrideFile(), f.getResultFile()));
            return files;
        } catch (IOException e) {
            log.error("Error while reading the directory {} due to following error {}", templatePath, e.getMessage());
            throw new TemplateMissingException("Error.IOException",
                    String.format("Error while reading the directory %s due to following error %s",
                            templatePath, e.getMessage()), e);
        }
    }
}
