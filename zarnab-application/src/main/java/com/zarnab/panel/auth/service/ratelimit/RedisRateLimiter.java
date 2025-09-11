package com.zarnab.panel.auth.service.ratelimit;

import com.zarnab.panel.auth.util.RedisKeyManager;
import com.zarnab.panel.common.exception.ZarnabException;
import com.zarnab.panel.core.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * An implementation of RateLimiter that uses Redis to track and limit attempts.
 */
@Component
@RequiredArgsConstructor
public class RedisRateLimiter implements RateLimiter {

    private final StringRedisTemplate redisTemplate;

    @Value("${zarnab.security.rate-limit.max-attempts:5}")
    private int maxAttempts;

    @Value("${zarnab.security.rate-limit.duration-seconds:300}")
    private long durationSeconds;

    /**
     * Checks and increments the verification attempt count for a given identifier.
     * If the number of attempts exceeds the configured maximum within the time window,
     * an exception is thrown.
     *
     * @param identifier A unique identifier for the action being rate-limited (e.g., a mobile number).
     * @throws ZarnabException if the rate limit has been exceeded.
     */
    @Override
    public void checkVerificationAttempt(String identifier) {
        String key = RedisKeyManager.getRateLimitVerifyKey(identifier);

        Long currentCount = redisTemplate.opsForValue().increment(key);

        if (currentCount != null) {
            // On the first attempt, set the expiration for the key.
            if (currentCount == 1) {
                redisTemplate.expire(key, durationSeconds, TimeUnit.SECONDS);
            }
            if (currentCount > maxAttempts) {
                throw new ZarnabException(ExceptionType.TOO_MANY_REQUESTS);
            }
        }
    }
}
