package com.zarnab.panel.auth.service.otp;

/**
 * A Strategy interface for generating One-Time Passwords (OTPs).
 * This allows different OTP generation strategies to be plugged in.
 */
public interface OtpGenerator {
    String generate();
}