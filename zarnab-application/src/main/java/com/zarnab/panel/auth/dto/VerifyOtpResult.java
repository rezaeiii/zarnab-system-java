package com.zarnab.panel.auth.dto;

/**
 * Represents the outcome of an OTP verification.
 * It can either be a successful login or a signal that registration is required.
 */
public record VerifyOtpResult(Status status, LoginResult loginResult, String registrationToken) {
    public enum Status {LOGIN_SUCCESS, REGISTRATION_REQUIRED}
}