package com.zarnab.panel.auth.service.otp;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum OtpPurpose {
    LOGIN_REGISTRATION("AUTH", "کد تایید برای ورود به حساب کاربری شما در زرناب: {0}\n\n@zarnab.kiwiapp.ir #{0}", 10, 120),
    INGOT_TRANSFER("INGOT_TRANSFER", "کد تایید برای انتقال شمش در زرناب: {0}\n\n@zarnab.kiwiapp.ir #{0}", 10, 120),
    CHANGE_MOBILE("CHANGE_MOBILE", "کد تایید برای تغییر شماره موبایل شما در زرناب: {0}\n\n@zarnab.kiwiapp.ir #{0}", 10, 120);

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
