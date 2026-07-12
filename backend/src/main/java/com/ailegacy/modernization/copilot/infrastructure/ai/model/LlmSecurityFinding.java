package com.ailegacy.modernization.copilot.infrastructure.ai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Deserialization target for one entry of the LLM's "findings" array.
 * Type/severity fields are kept as raw strings and normalized leniently in
 * {@link com.ailegacy.modernization.copilot.infrastructure.ai.SecurityAnalyzer}.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LlmSecurityFinding {

    private String issueType;
    private String title;
    private String description;
    private String severity;
    private Integer riskScore;
    private String location;
    private String recommendation;
    private String modernAlternative;
    private List<String> evidence;

}
