package com.ailegacy.modernization.copilot.infrastructure.ai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Deserialization target for one entry of the LLM's "requiredTechnologies" array.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LlmRequiredTechnology {

    private String technology;
    private Boolean recommended;
    private String reason;

}
