package com.zarnab.panel.common.annotation.fileValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.zarnab.panel.common.translate.Translator.translate;


public class FileValidator implements ConstraintValidator<FileConstraint, Object> {
    private long maxSize;
    private List<String> allowedTypesList;
    private int maxFiles;


    public void initialize(FileConstraint constraint) {
        this.maxSize = parseSizeToBytes(constraint.maxSize());
        this.allowedTypesList = Arrays.asList(constraint.allowedTypes());
        this.maxFiles = constraint.maxFiles();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true; // optional, or false if required

        List<MultipartFile> files;
        if (value instanceof MultipartFile file) {
            files = List.of(file);
        } else if (value instanceof List<?> list && list.stream().allMatch(o -> o instanceof MultipartFile)) {
            files = (List<MultipartFile>) list;
        } else {
            return false; // unsupported type
        }

        if (files.size() > maxFiles) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    translate("error.validation.maxFilesExceeded", maxFiles)
            ).addConstraintViolation();
            return false;
        }

        for (MultipartFile file : files) {
            if (file.getSize() > maxSize) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        translate("error.validation.maxSizeExceeded", file.getOriginalFilename(), formatSize(maxSize))
                ).addConstraintViolation();
                return false;
            }

            String contentType = file.getContentType();
            if (!allowedTypesList.isEmpty() && (contentType == null || !allowedTypesList.contains(contentType))) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                        translate("error.validation.invalidFileType", file.getOriginalFilename(), String.join(", ", allowedTypesList))
                ).addConstraintViolation();
                return false;
            }
        }

        return true;
    }

    private long parseSizeToBytes(String size) {
        Pattern pattern = Pattern.compile("^(\\d+)(B|KB|MB|GB)?$", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(size.trim());

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid size format: " + size);
        }

        long value = Long.parseLong(matcher.group(1));
        String unit = matcher.group(2);

        if (unit == null || unit.equalsIgnoreCase("B")) {
            return value;
        } else if (unit.equalsIgnoreCase("KB")) {
            return value * 1024;
        } else if (unit.equalsIgnoreCase("MB")) {
            return value * 1024 * 1024;
        } else if (unit.equalsIgnoreCase("GB")) {
            return value * 1024 * 1024 * 1024;
        }

        throw new IllegalArgumentException("Unknown size unit: " + unit);
    }

    private String formatSize(long size) {
        if (size >= 1024 * 1024) {
            return (size / (1024 * 1024)) + " MB";
        } else if (size >= 1024) {
            return (size / 1024) + " KB";
        } else {
            return size + " B";
        }
    }
}