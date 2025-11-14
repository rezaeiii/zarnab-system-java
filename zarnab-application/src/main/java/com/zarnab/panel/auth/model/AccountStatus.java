package com.zarnab.panel.auth.model;

/**
 * Represents the operational status of a user's account.
 */
public enum AccountStatus {
    /**
     * The account is active and fully operational.
     */
    ACTIVE,

    /**
     * The account is temporarily locked due to a pending inheritance process.
     * All asset transfers and profile changes should be disabled.
     */
    LOCKED_INHERITANCE_PENDING,

    /**
     * The account has been permanently closed.
     */
    CLOSED
}
