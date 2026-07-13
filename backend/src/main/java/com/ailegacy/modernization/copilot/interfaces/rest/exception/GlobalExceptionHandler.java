package com.ailegacy.modernization.copilot.interfaces.rest.exception;

import com.ailegacy.modernization.copilot.domain.exceptions.*;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.ApiResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.ErrorFieldDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

/**
 * Global exception handler for all REST endpoints.
 *
 * Catches and formats exceptions into standardized API responses:
 * - Domain exceptions
 * - Validation exceptions
 * - Authentication/Authorization exceptions
 * - Unexpected runtime exceptions
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse<?> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ApiResponse.error(ex.getMessage(), ex.getErrorCode());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleValidationException(ValidationException ex) {
        log.warn("Validation failed: {}", ex.getMessage());
        return ApiResponse.error(ex.getMessage(), ex.getErrorCode());
    }

    @ExceptionHandler(BusinessLogicException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ApiResponse<?> handleBusinessLogicException(BusinessLogicException ex) {
        log.warn("Business logic violation: {}", ex.getMessage());
        return ApiResponse.error(ex.getMessage(), ex.getErrorCode());
    }

    @ExceptionHandler(UnauthorizedException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse<?> handleUnauthorizedException(UnauthorizedException ex) {
        log.warn("Unauthorized access attempt: {}", ex.getMessage());
        return ApiResponse.error(ex.getMessage(), ex.getErrorCode());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiResponse<?> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ApiResponse.error(ex.getMessage(), ex.getErrorCode());
    }

    @ExceptionHandler(DomainException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleDomainException(DomainException ex) {
        log.warn("Domain exception: {}", ex.getMessage());
        return ApiResponse.error(ex.getMessage(), ex.getErrorCode());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<ErrorFieldDto> errors = new ArrayList<>();
        
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            Object rejectedValue = ((FieldError) error).getRejectedValue();
            
            errors.add(ErrorFieldDto.builder()
                    .field(fieldName)
                    .message(errorMessage)
                    .rejectedValue(rejectedValue)
                    .build());
        });

        log.warn("Request validation failed with {} errors", errors.size());
        return ApiResponse.error(
                "Request validation failed",
                "VALIDATION_ERROR",
                errors.stream().map(e -> e.getField() + ": " + e.getMessage()).toList()
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        log.warn("Malformed request body: {}", ex.getMessage());
        return ApiResponse.error("Request body is malformed or contains an invalid value", "MALFORMED_REQUEST");
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<?> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return ApiResponse.error(
                "An unexpected error occurred",
                "INTERNAL_SERVER_ERROR"
        );
    }

}
