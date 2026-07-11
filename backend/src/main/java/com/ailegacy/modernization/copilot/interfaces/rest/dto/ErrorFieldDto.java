package com.ailegacy.modernization.copilot.interfaces.rest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Standard error response DTO for validation and constraint violations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorFieldDto {

    private String field;
    private String message;
    private Object rejectedValue;

}
