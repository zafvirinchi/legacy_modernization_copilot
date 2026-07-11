package com.ailegacy.modernization.copilot.domain.exceptions;

/**
 * Exception thrown when an operation is forbidden due to insufficient permissions.
 */
public class AccessDeniedException extends DomainException {

    public AccessDeniedException(String message) {
        super(message, "ACCESS_DENIED");
    }

    public AccessDeniedException(String message, Throwable cause) {
        super(message, "ACCESS_DENIED", cause);
    }

}
