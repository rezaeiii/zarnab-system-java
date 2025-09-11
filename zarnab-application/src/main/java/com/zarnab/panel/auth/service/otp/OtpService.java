package com.zarnab.panel.auth.service.otp;

/**
 * A dedicated service for managing the entire lifecycle of One-Time Passwords (OTPs).
 * This includes generation, sending, validation, and enforcing security policies like cooldowns.
 */
public interface OtpService {

    /**
     * Generates, stores, and sends an OTP to the given mobile number.
     * Enforces a cooldown period to prevent abuse and repeated requests.
     *
     * @param mobileNumber The target mobile number to send the OTP to.
     * @throws com.zarnab.panel.common.exception.ZarnabException if the cooldown period is still active.
     */
    void sendOtp(String mobileNumber);

    /**
     * Verifies the provided OTP against the stored one for the given mobile number.
     * If verification is successful, the OTP is consumed and cannot be used again.
     *
     * @param mobileNumber The mobile number associated with the OTP.
     * @param otp          The 6-digit code provided by the user.
     * @throws org.springframework.security.authentication.BadCredentialsException if the OTP is invalid or expired.
     */
    void verifyOtp(String mobileNumber, String otp);
}