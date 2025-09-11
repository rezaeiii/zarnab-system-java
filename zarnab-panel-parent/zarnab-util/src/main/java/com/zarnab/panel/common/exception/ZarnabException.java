package com.zarnab.panel.common.exception;

import lombok.Getter;

/**
 * A general, application-wide exception that is created from a predefined ErrorCode.
 * It can also hold dynamic arguments for message formatting.
 */
@Getter
public class ZarnabException extends RuntimeException {

    private final IZarnabException exceptionType;
    private final transient Object[] args;

    /**
     * Constructs a new ApiException with a specific error code.
     *
     * @param exceptionType The predefined error code from the ErrorCode enum.
     */
    public ZarnabException(IZarnabException exceptionType) {
        super(exceptionType.getMessageKey());
        this.exceptionType = exceptionType;
        this.args = new Object[0];
    }

    /**
     * Constructs a new ApiException with a specific error code and dynamic arguments for the message.
     *
     * @param exceptionType The predefined error code.
     * @param args          Dynamic arguments to be formatted into the error message (e.g., a user ID).
     */
    public ZarnabException(IZarnabException exceptionType, Object... args) {
        super(exceptionType.getMessageKey());
        this.exceptionType = exceptionType;
        this.args = args;
    }
}