package com.zarnab.panel.common.translate;

import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Locale;

public class LanguageUtil {

    public static Locale getCurrentLocale() {
        return LocaleContextHolder.getLocale();
    }

    public static boolean isJalali() {
        return getCurrentLocale().getLanguage().equals("fa");
    }
}