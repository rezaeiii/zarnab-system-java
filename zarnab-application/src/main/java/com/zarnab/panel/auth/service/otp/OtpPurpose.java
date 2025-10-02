package com.zarnab.panel.auth.service.otp;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OtpPurpose {
    LOGIN_REGISTRATION("AUTH", "Your verification code is: {0}", 60, 120),
    INGOT_TRANSFER("INGOT_TRANSFER", "Your ingot transfer code is: {0}", 60, 120),
    CHANGE_MOBILE("CHANGE_MOBILE", "Your mobile change code is: {0}", 60, 120);

    private final String code;
    private final String messageTemplate;
    private final int cooldownSeconds;
    private final int expirationSeconds;

    public String formatMessage(String otp) {
        return messageTemplate.replace("{0}", otp);
    }

    public long cooldownSeconds() {
        return cooldownSeconds;
    }

    public long expirationSeconds() {
        return expirationSeconds;
    }

    public String code() {
        return code;
    }
}
