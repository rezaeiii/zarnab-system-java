package com.zarnab.panel.common.file.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = MinioUrlSerializer.class)
public @interface MinioUrl {
    String folderPath() default "";

    int expire() default 0;

    boolean onlyDownload() default true;

    boolean videoStream() default false;
}