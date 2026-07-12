package com.ailegacy.modernization.copilot.infrastructure.ai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Deserialization target for the Spring Boot generator's expected LLM response shape.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LlmGeneratedCodePayload {

    private String sourceServletReference;
    private String sourceJdbcReference;
    private String entityCode;
    private String repositoryCode;
    private String dtoCode;
    private String serviceCode;
    private String controllerCode;
    private String explanation;

}
