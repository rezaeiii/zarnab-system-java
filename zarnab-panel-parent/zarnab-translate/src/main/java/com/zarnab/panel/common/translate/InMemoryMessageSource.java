package com.zarnab.panel.common.translate;

import org.springframework.context.support.AbstractMessageSource;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryMessageSource extends AbstractMessageSource {

    private final Map<String, Map<Locale, String>> messages = new ConcurrentHashMap<>();
    private final Locale defaultLocale;

    /**
     * Constructor now takes the default locale as a dependency and loads all messages upon initialization.
     *
     * @param defaultLocale The application's default locale, used for fallbacks.
     */
    public InMemoryMessageSource(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
        // Load messages for all supported languages
        List.of(Locale.ENGLISH, Locale.of("fa"), Locale.FRENCH).forEach(this::loadFromResourceBundle);
    }

    private void loadFromResourceBundle(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle("i18n/messages", locale);
        bundle.keySet().forEach(key ->
                messages.computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                        .put(locale, bundle.getString(key))
        );
    }

    /**
     * Resolves the message for a given code and locale.
     * 1. Tries to find the message for the requested locale.
     * 2. If not found, it falls back to the configured default locale.
     * 3. If still not found, returns null (Spring's default behavior will then apply).
     */
    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        Map<Locale, String> localizedMessages = messages.get(code);
        if (localizedMessages == null) {
            return null;
        }

        String message = localizedMessages.get(locale);
        if (message == null) {
            message = localizedMessages.get(this.defaultLocale); // Fallback to default
        }

        return (message != null) ? new MessageFormat(message, locale) : null;
    }
}