package com.zarnab.panel.common.translate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.List;
import java.util.Locale;

@Slf4j
@Configuration
public class TranslationConfig {

    /**
     * Determines the default locale for the application.
     * It first tries to use the 'ime.language' property from your application.yml/properties.
     * If the property is not set, is empty, or is invalid, it defaults to "fa" (Persian).
     *
     * @param defaultLanguageCode The language code from properties, defaulting to an empty string if not provided.
     * @return The resolved default Locale.
     */
    @Bean
    public Locale defaultLocale(@Value("${ime.language:}") String defaultLanguageCode) {
        if (defaultLanguageCode != null && !defaultLanguageCode.isBlank()) {
            try {
                return Locale.forLanguageTag(defaultLanguageCode);
            } catch (Exception e) {
                log.warn("Invalid language code '{}' in 'ime.language' property. Falling back to 'fa'.", defaultLanguageCode, e);
            }
        }
        // Fallback locale if property is missing, empty, or invalid
        return Locale.of("fa");
    }

    /**
     * Configures the resolver that determines the user's locale from the 'Accept-Language' HTTP header.
     * It sets the default locale determined by the defaultLocale() bean.
     *
     * @param defaultLocale The application's default Locale.
     * @return The configured LocaleContextResolver.
     */
    @Bean
    public LocaleResolver localeResolver(Locale defaultLocale) {
        AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(defaultLocale);
        // Optionally, you can define a list of supported locales
        resolver.setSupportedLocales(List.of(Locale.ENGLISH, Locale.of("fa")));
        return resolver;
    }

    /**
     * Creates the custom in-memory MessageSource bean.
     * It's responsible for loading and resolving translation messages.
     *
     * @param defaultLocale The application's default Locale.
     * @return The configured InMemoryMessageSource.
     */
    @Bean
    public MessageSource messageSource(Locale defaultLocale) {
        return new InMemoryMessageSource(defaultLocale);
    }
}