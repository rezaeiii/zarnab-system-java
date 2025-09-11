package com.zarnab.panel.auth.util;

/**
 * A utility class to centralize the generation of Redis keys.
 * This prevents key collisions and makes the key schema manageable.
 * Using a final class with a private constructor is a standard practice for utility classes.
 */
public final class RedisKeyManager {

    private static final String OTP_KEY_PREFIX = "otp:";
    private static final String OTP_COOLDOWN_KEY_PREFIX = "otp_cooldown:";
    private static final String RATE_LIMIT_VERIFY_PREFIX = "rate_limit:verify:";
    private static final String REG_TOKEN_KEY_PREFIX = "reg_token:";

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private RedisKeyManager() {}

    public static String getOtpKey(String mobileNumber) {
        return OTP_KEY_PREFIX + mobileNumber;
    }

    public static String getOtpCooldownKey(String mobileNumber) {
        return OTP_COOLDOWN_KEY_PREFIX + mobileNumber;
    }

    public static String getRateLimitVerifyKey(String identifier) {
        return RATE_LIMIT_VERIFY_PREFIX + identifier;
    }

    public static String getRegistrationTokenKey(String token) {
        return REG_TOKEN_KEY_PREFIX + token;
    }
}
