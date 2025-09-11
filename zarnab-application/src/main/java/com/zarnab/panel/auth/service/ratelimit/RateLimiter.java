package com.zarnab.panel.auth.service.ratelimit;

/**
 * An interface with declarative methods for rate limiting specific business actions.
 * The implementation handles the details of key creation and storage.
 */
public interface RateLimiter {
    void checkVerificationAttempt(String identifier);
}