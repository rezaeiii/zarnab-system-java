package com.zarnab.panel.auth.service.otp;

import com.zarnab.panel.auth.service.sms.SmsService;
import com.zarnab.panel.auth.service.token.TokenStore;
import com.zarnab.panel.auth.util.RedisKeyManager;
import com.zarnab.panel.common.exception.ZarnabException;
import com.zarnab.panel.core.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Default implementation of the OtpService using Redis for storage and cooldowns.
 * This version incorporates best practices such as externalized configuration and atomic operations.
 */
@Service
@RequiredArgsConstructor
public class RedisOtpService implements OtpService {

    private final OtpGenerator otpGenerator;
    private final TokenStore tokenStore;
    private final SmsService smsService;

    // --- Configuration injected from application.properties (in seconds) ---

    @Value("${zarnab.security.otp.cooldown-seconds:120}")
    private long cooldownSeconds;

    @Value("${zarnab.security.otp.expiration-seconds:300}")
    private long otpExpirationSeconds;

    /**
     * Sends an OTP to the user's mobile number after ensuring a cooldown period is respected.
     * The cooldown check is performed atomically to prevent race conditions.
     *
     * @param mobileNumber The target mobile number.
     * @throws ZarnabException if a request is made during the cooldown period.
     */
    @Override
    public void sendOtp(String mobileNumber) {
        String cooldownKey = RedisKeyManager.getOtpCooldownKey(mobileNumber);

        // Atomically set the cooldown key. If it's already present, storeIfAbsent returns false.
        boolean canSend = tokenStore.storeIfAbsent(cooldownKey, "active", cooldownSeconds, TimeUnit.SECONDS);
        if (!canSend) {
            throw new ZarnabException(ExceptionType.TOO_MANY_REQUESTS);
        }

        String otp = otpGenerator.generate();
        String otpKey = RedisKeyManager.getOtpKey(mobileNumber);

        // Store the generated OTP with its own expiration.
        tokenStore.store(otpKey, otp, otpExpirationSeconds, TimeUnit.SECONDS);
        smsService.sendSms(mobileNumber, "Zarnab Panel Code: " + otp);
    }

    /**
     * Verifies the provided OTP against the one stored in Redis.
     * If verification is successful, the OTP key is consumed (deleted).
     *
     * @param mobileNumber The mobile number to verify.
     * @param otp The OTP provided by the user.
     * @throws BadCredentialsException if the OTP is invalid, expired, or not found.
     */
    @Override
    public void verifyOtp(String mobileNumber, String otp) {
        String otpKey = RedisKeyManager.getOtpKey(mobileNumber);

        String storedOtp = tokenStore.retrieve(otpKey)
                .orElseThrow(() -> new BadCredentialsException("Invalid or expired OTP."));

        if (!storedOtp.equals(otp)) {
            throw new BadCredentialsException("Invalid OTP.");
        }

        // Consume the OTP upon successful verification to prevent reuse.
        tokenStore.consume(otpKey);
    }
}
