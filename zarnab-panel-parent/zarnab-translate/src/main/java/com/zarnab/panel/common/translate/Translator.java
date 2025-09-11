package com.zarnab.panel.common.translate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Translator {

    /**
     * This static field holds the MessageSource instance.
     * It's populated by the constructor when Spring creates the Translator bean.
     */
    private static MessageSource messageSource;

    /**
     * The constructor is called by Spring during application startup.
     * It injects the configured MessageSource bean and assigns it to our static field.
     * This is the bridge between Spring's dependency injection world and our static access world.
     *
     * @param source The MessageSource bean from your TranslationConfig.
     */
    public Translator(MessageSource source) {
        Translator.messageSource = source;
    }

    /**
     * Translates a message key using the locale from the current request context.
     *
     * @param key The message key (e.g., "user.welcome").
     * @return The translated string.
     */
    public static String translate(String key) {
        if (messageSource == null) {
            log.warn("MessageSource is not initialized. Cannot translate key '{}'.", key);
            return key; // Fallback to returning the key itself
        }
        return messageSource.getMessage(key, null, LocaleContextHolder.getLocale());
    }

    /**
     * Translates and formats a message key with dynamic arguments.
     *
     * @param key  The message key (e.g., "user.greeting").
     * @param args The arguments to format the message with (e.g., a username).
     * @return The translated and formatted string.
     */
    public static String translate(String key, Object... args) {
        if (messageSource == null) {
            log.warn("MessageSource is not initialized. Cannot translate key '{}' with args.", key);
            return key; // Fallback to returning the key itself
        }
        return messageSource.getMessage(key, args, LocaleContextHolder.getLocale());
    }
}