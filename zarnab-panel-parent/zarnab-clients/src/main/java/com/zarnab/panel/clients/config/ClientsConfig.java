package com.zarnab.panel.clients.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "api.clients")
public record ClientsConfig(
        Uid uid,
        Sms sms,
        Retry retry) {

    public record Uid(
            @NotBlank String baseUrl,
            @NotBlank String businessId,
            @NotBlank String businessToken,
            @Positive int timeoutMs) {
    }

    public record Sms(
            @NotBlank String baseUrl,
            @NotBlank String apiKey,
            @NotNull Long lineNumber,
            @Positive int timeoutMs) {
    }

    public record Retry(@Positive int maxAttempts, @Positive int backoffMs) {
    }

}
