package com.ailegacy.modernization.copilot.domain.exceptions;

/**
 * Exception thrown when a business operation violates domain invariants.
 */
public class BusinessLogicException extends DomainException {

    public BusinessLogicException(String message) {
        super(message, "BUSINESS_LOGIC_ERROR");
    }

    public BusinessLogicException(String message, String errorCode) {
        super(message, errorCode);
    }

    public BusinessLogicException(String message, Throwable cause) {
        super(message, "BUSINESS_LOGIC_ERROR", cause);
    }

    public BusinessLogicException(String message, String errorCode, Throwable cause) {
        super(message, errorCode, cause);
    }

}
