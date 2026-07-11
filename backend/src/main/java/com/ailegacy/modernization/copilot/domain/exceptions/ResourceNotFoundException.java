package com.ailegacy.modernization.copilot.domain.exceptions;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends DomainException {

    public ResourceNotFoundException(String message) {
        super(message, "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String resourceName, String resourceId) {
        super(String.format("%s with ID '%s' not found", resourceName, resourceId), "RESOURCE_NOT_FOUND");
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, "RESOURCE_NOT_FOUND", cause);
    }

}
