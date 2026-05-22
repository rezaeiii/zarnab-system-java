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

    public static String toPersianNumber(String number) {
        return number.replaceAll("0", "۰")
                .replaceAll("1", "۱")
                .replaceAll("2", "۲")
                .replaceAll("3", "۳")
                .replaceAll("4", "۴")
                .replaceAll("5", "۵")
                .replaceAll("6", "۶")
                .replaceAll("7", "۷")
                .replaceAll("8", "۸")
                .replaceAll("9", "۹");
    }

}