package com.zarnab.panel.auth.model;

/**
 * Represents the roles a user can have within the system.
 * Based on the provided business diagram.
 */
public enum Role {
    /**
     * superuser with all permissions.
     */
    ADMIN,

    /**
     * A representative who can be a natural or legal person.
     * (نماینده)
     */
    REPRESENTATIVE,

    /**
     * A counter user.
     * (کانتر)
     */
    COUNTER,

    /**
     * A first-hand buyer.
     * (خریدار دست اول)
     */
    BUYER,

    /**
     * A standard user with basic permissions.
     */
    USER
}

