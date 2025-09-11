package com.zarnab.panel.auth.service.token;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * An abstraction for a key-value store for handling ephemeral tokens like OTPs.
 */
public interface TokenStore {

    /**
     * Stores a value with an expiration. Overwrites the key if it exists.
     */
    void store(String key, String value, long timeout, TimeUnit unit);

    /**
     * Stores a key-value pair only if the key does not already exist.
     * This is useful for atomic operations like setting a cooldown lock.
     *
     * @return true if the key was set, false otherwise.
     */
    boolean storeIfAbsent(String key, String value, long timeout, TimeUnit unit);

    /**
     * Retrieves a value by its key.
     */
    Optional<String> retrieve(String key);

    /**
     * Retrieves and deletes a value atomically.
     */
    Optional<String> consume(String key);


    /**
     * Gets the remaining expiration time for a key.
     *
     * @param key The key to check.
     * @param unit The time unit for the returned value.
     * @return The remaining time, or an empty Optional if the key does not exist or has no expiration.
     */
    Optional<Long> getExpirationTime(String key, TimeUnit unit);
}
