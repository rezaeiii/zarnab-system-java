package com.zarnab.panel.auth.service.otp;

import java.util.Arrays;

public enum OtpPurpose {
    AUTH("auth", "Zarnab Auth Code: {otp}", 120, 300),
    INGOT_TRANSFER("ingot-transfer", "Ingot transfer confirmation code: {otp}", 120, 300),
    GENERIC("generic", "Your verification code: {otp}", 120, 300);

    private final String code;
    private final String messageTemplate;
    private final long cooldownSeconds;
    private final long expirationSeconds;

    OtpPurpose(String code, String messageTemplate, long cooldownSeconds, long expirationSeconds) {
        this.code = code;
        this.messageTemplate = messageTemplate;
        this.cooldownSeconds = cooldownSeconds;
        this.expirationSeconds = expirationSeconds;
    }

    public String code() {
        return code;
    }

    public String formatMessage(String otp) {
        return messageTemplate.replace("{otp}", otp);
    }

    public long cooldownSeconds() {
        return cooldownSeconds;
    }

    public long expirationSeconds() {
        return expirationSeconds;
    }

    public static OtpPurpose fromString(String value) {
        if (value == null || value.isBlank()) {
            return GENERIC;
        }
        String normalized = value.trim().toLowerCase();
        return Arrays.stream(values())
                .filter(p -> p.code.equalsIgnoreCase(normalized) || p.name().equalsIgnoreCase(normalized))
                .findFirst()
                .orElse(GENERIC);
    }
} 