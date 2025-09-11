package com.zarnab.panel.common.annotation.friendlyDate;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.zarnab.panel.common.constants.ConfigConstants.DATE_TIME_PATTERN;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@JacksonAnnotationsInside
@JsonSerialize(using = FriendlyDateSerializer.class)
public @interface FriendlyDate {


    /**
     * Define the date format pattern when includeTime=true.
     * Default: "yyyy-MM-dd HH:mm:ss"
     */
    String pattern() default DATE_TIME_PATTERN;

    /**
     * Define whether to use Jalali (true) or Gregorian (false) as the default calendar.
     * Default: true (Jalali)
     */
    FriendlyDateType type() default FriendlyDateType.Header;

    /**
     * Define if time should be included.
     * Default: true (includes time)
     */
    boolean includeTime() default true;

    /**
     * Define time zone for conversion.
     * Default: Asia/Tehran
     */
    String timeZone() default "Asia/Tehran";

    /**
     * Define a custom field name for the friendly date.
     * Default: "{originalFieldName}Friendly"
     */
    String fieldName() default "";

}