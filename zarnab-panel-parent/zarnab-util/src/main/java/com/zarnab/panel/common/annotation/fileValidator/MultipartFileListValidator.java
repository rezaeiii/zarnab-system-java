package com.zarnab.panel.common.annotation.fileValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;

public class MultipartFileListValidator implements ConstraintValidator<FileConstraint, List<MultipartFile>> {
    private long maxSize;
    private List<String> allowedTypesList;
    private int maxFiles;

    @Override
    public void initialize(FileConstraint constraint) {
        this.maxSize = parseSizeToBytes(constraint.maxSize());
        this.allowedTypesList = Arrays.asList(constraint.allowedTypes());
        this.maxFiles = constraint.maxFiles();
    }

    @Override
    public boolean isValid(List<MultipartFile> files, ConstraintValidatorContext context) {
        if (files == null || files.isEmpty()) return true;

        if (files.size() > maxFiles) {
            buildMessage(context, "Too many files, max allowed: " + maxFiles);
            return false;
        }

        for (MultipartFile file : files) {
            if (file.getSize() > maxSize) {
                buildMessage(context, "File " + file.getOriginalFilename() + " exceeds " + formatSize(maxSize));
                return false;
            }

            if (!allowedTypesList.isEmpty() &&
                    (file.getContentType() == null || !allowedTypesList.contains(file.getContentType()))) {
                buildMessage(context, "Invalid file type for: " + file.getOriginalFilename());
                return false;
            }
        }

        return true;
    }

    private long parseSizeToBytes(String size) {
        if (size.toLowerCase().endsWith("mb")) {
            return Long.parseLong(size.replace("MB", "").trim()) * 1024 * 1024;
        } else if (size.toLowerCase().endsWith("kb")) {
            return Long.parseLong(size.replace("KB", "").trim()) * 1024;
        } else if (size.toLowerCase().endsWith("gb")) {
            return Long.parseLong(size.replace("GB", "").trim()) * 1024 * 1024 * 1024;
        } else {
            return Long.parseLong(size.replace("B", "").trim());
        }
    }

    private String formatSize(long size) {
        if (size >= 1024 * 1024) return (size / (1024 * 1024)) + " MB";
        if (size >= 1024) return (size / 1024) + " KB";
        return size + " B";
    }

    private void buildMessage(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
