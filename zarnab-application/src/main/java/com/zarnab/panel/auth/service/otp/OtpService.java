package com.zarnab.panel.auth.service.otp;

/**
 * A dedicated service for managing the entire lifecycle of One-Time Passwords (OTPs).
 * This includes generation, sending, validation, and enforcing security policies like cooldowns.
 */
public interface OtpService {

    /**
     * Primary send method using purpose and mobile number.
     */
    void sendOtp(OtpPurpose purpose, String mobileNumber);

    /**
     * Primary verify method using purpose and mobile number.
     *
     * @return
     */
    boolean verifyOtp(OtpPurpose purpose, String mobileNumber, String otp);

    /**
     * Clears the OTP and its cooldown for a specific purpose and mobile number.
     */
    void clearOtp(OtpPurpose purpose, String mobileNumber);


}