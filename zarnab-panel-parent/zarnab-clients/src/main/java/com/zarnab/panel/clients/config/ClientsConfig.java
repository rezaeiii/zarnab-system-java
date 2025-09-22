package com.zarnab.panel.clients.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

@Validated
@ConfigurationProperties(prefix = "api.clients")
public record ClientsConfig(
        Uid uid,
        Faraboom faraboom,
        Retry retry) {

    public record Uid(
            @NotBlank String baseUrl,
            @NotBlank String businessId,
            @NotBlank String businessToken,
            @Positive int timeoutMs) {
    }


    public record Faraboom(
            @NotBlank String baseUrl,
            @NotBlank String appKey,
            @NotBlank String appSecret,
            @NotBlank String deviceId,
            @NotBlank String bankId,
            @NotBlank String tokenId,
            @NotBlank String clientDeviceId,
            @NotBlank String clientIpAddress,
            @NotBlank String clientUserAgent,
            @NotBlank String clientUserId,
            @NotBlank String clientPlatformType,
            @Positive int timeoutMs,
            // OAuth properties
            @NotBlank String oauthGrantType,
            @NotBlank String oauthUsername,
            @NotBlank String oauthPassword,
            @NotBlank String oauthTokenPath
            ) {
    }

    public record Retry(@Positive int maxAttempts, @Positive int backoffMs) {
    }

}