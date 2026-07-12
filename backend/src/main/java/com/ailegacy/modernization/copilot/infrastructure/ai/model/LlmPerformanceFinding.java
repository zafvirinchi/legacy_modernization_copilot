package com.ailegacy.modernization.copilot.infrastructure.ai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Deserialization target for one entry of the LLM's "findings" array.
 * The type field is kept as a raw string and normalized leniently in
 * {@link com.ailegacy.modernization.copilot.infrastructure.ai.PerformanceAnalyzer}.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LlmPerformanceFinding {

    private String issueType;
    private String title;
    private String description;
    private String location;
    private String optimizationSuggestion;
    private String modernAlternative;
    private List<String> evidence;

}
