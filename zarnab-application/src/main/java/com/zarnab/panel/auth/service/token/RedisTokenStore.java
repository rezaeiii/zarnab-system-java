package com.zarnab.panel.auth.service.token;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * A TokenStore implementation using Redis as the backend.
 */
@Component
@RequiredArgsConstructor
public class RedisTokenStore implements TokenStore {

    private final StringRedisTemplate redisTemplate;

    @Override
    public void store(String key, String value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    @Override
    public boolean storeIfAbsent(String key, String value, long timeout, TimeUnit unit) {
        Boolean wasSet = redisTemplate.opsForValue().setIfAbsent(key, value, timeout, unit);
        return wasSet != null && wasSet;
    }

    @Override
    public Optional<String> retrieve(String key) {
        return Optional.ofNullable(redisTemplate.opsForValue().get(key));
    }

    @Override
    public Optional<String> consume(String key) {
        // Redis GETDEL is atomic, perfect for consuming a token.
        String value = redisTemplate.opsForValue().getAndDelete(key);
        return Optional.ofNullable(value);
    }
}
