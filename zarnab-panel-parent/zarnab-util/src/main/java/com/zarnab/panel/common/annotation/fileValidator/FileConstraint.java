package com.zarnab.panel.common.annotation.fileValidator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = {MultipartFileValidator.class, MultipartFileListValidator.class})
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface FileConstraint {
    String message() default "Invalid file";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String maxSize() default "10MB";

    String[] allowedTypes() default {};

    int maxFiles() default Integer.MAX_VALUE;
}