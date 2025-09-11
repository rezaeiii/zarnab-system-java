package com.zarnab.panel.auth.service.otp;

import org.springframework.stereotype.Component;
import java.security.SecureRandom;

@Component
public class NumericOtpGenerator implements OtpGenerator {

    private static final SecureRandom random = new SecureRandom();
    private static final int OTP_LENGTH = 6;
    private static final int MAX_VALUE = (int) Math.pow(10, OTP_LENGTH);

    @Override
    public String generate() {
        int otpValue = random.nextInt(MAX_VALUE);
        return String.format("%0" + OTP_LENGTH + "d", otpValue);
    }
}