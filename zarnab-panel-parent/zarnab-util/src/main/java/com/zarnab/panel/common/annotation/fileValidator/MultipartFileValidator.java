package com.zarnab.panel.common.annotation.fileValidator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultipartFileValidator implements ConstraintValidator<FileConstraint, MultipartFile> {
    private long maxSize;
    private List<String> allowedTypesList;

    @Override
    public void initialize(FileConstraint constraint) {
        this.maxSize = parseSizeToBytes(constraint.maxSize());
        this.allowedTypesList = Arrays.asList(constraint.allowedTypes());
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        if (file == null) return true;

        if (file.getSize() > maxSize) {
            buildMessage(context, "File size exceeds max allowed: " + formatSize(maxSize));
            return false;
        }

        if (!allowedTypesList.isEmpty() &&
                (file.getContentType() == null || !allowedTypesList.contains(file.getContentType()))) {
            buildMessage(context, "Invalid file type: " + file.getContentType());
            return false;
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

        if (unit == null || unit.equalsIgnoreCase("B")) return value;
        if (unit.equalsIgnoreCase("KB")) return value * 1024;
        if (unit.equalsIgnoreCase("MB")) return value * 1024 * 1024;
        if (unit.equalsIgnoreCase("GB")) return value * 1024 * 1024 * 1024;

        throw new IllegalArgumentException("Unknown size unit: " + unit);
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
