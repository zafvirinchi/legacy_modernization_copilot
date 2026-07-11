package com.ailegacy.modernization.copilot.domain.exceptions;

/**
 * Exception thrown when an unauthorized operation is attempted.
 */
public class UnauthorizedException extends DomainException {

    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED");
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, "UNAUTHORIZED", cause);
    }

}
