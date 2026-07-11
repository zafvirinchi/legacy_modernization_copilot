package com.ailegacy.modernization.copilot.domain.exceptions;

/**
 * Exception thrown when validation of input data fails.
 */
public class ValidationException extends DomainException {

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }

    public ValidationException(String message, Throwable cause) {
        super(message, "VALIDATION_ERROR", cause);
    }

}
