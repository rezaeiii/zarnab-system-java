package com.zarnab.panel.auth.service.otp;

import com.zarnab.panel.auth.service.sms.SmsService;
import com.zarnab.panel.auth.service.token.TokenStore;
import com.zarnab.panel.auth.util.RedisKeyManager;
import com.zarnab.panel.common.exception.ZarnabException;
import com.zarnab.panel.core.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Default implementation of the OtpService using Redis for storage and cooldowns.
 */
@Service
@RequiredArgsConstructor
public class RedisOtpService implements OtpService {

    private final OtpGenerator otpGenerator;
    private final TokenStore tokenStore;
    private final SmsService smsService;

    @Override
    public void sendOtp(OtpPurpose purpose, String mobileNumber) {
        String cooldownKey = RedisKeyManager.getOtpCooldownKey(purpose.code(), mobileNumber);

        boolean canSend = tokenStore.storeIfAbsent(cooldownKey, "active", purpose.cooldownSeconds(), TimeUnit.SECONDS);
        if (!canSend) {
            long remainingSeconds = tokenStore.getExpirationTime(cooldownKey, TimeUnit.SECONDS)
                    .orElse(purpose.cooldownSeconds());
            throw new ZarnabException(ExceptionType.TOO_MANY_REQUESTS, remainingSeconds);
        }

        String otp = otpGenerator.generate();
        String otpKey = RedisKeyManager.getOtpKey(purpose.code(), mobileNumber);
        tokenStore.store(otpKey, otp, purpose.expirationSeconds(), TimeUnit.SECONDS);
        smsService.sendSms(mobileNumber, purpose.formatMessage(otp));
    }

    @Override
    public void verifyOtp(OtpPurpose purpose, String mobileNumber, String otp) {
        String otpKey = RedisKeyManager.getOtpKey(purpose.code(), mobileNumber);
        String storedOtp = tokenStore.retrieve(otpKey)
                .orElseThrow(() -> new BadCredentialsException("Invalid or expired OTP."));
        if (!storedOtp.equals(otp)) {
            throw new BadCredentialsException("Invalid OTP.");
        }
        tokenStore.consume(otpKey);
    }
}
