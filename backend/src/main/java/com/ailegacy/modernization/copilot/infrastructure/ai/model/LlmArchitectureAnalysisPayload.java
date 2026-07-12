package com.ailegacy.modernization.copilot.infrastructure.ai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Deserialization target for the architecture analyzer's expected LLM response
 * shape. Pattern fields are kept as raw strings (not enums) since LLM output
 * formatting can't be guaranteed to match an exact enum token; normalization
 * happens in {@link com.ailegacy.modernization.copilot.infrastructure.ai.ArchitectureAnalyzer}.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LlmArchitectureAnalysisPayload {

    private String detectedPattern;
    private String currentArchitectureDescription;
    private String currentArchitectureDiagram;
    private Integer architectureScore;
    private String architectureScoreJustification;
    private List<String> recommendations;
    private String targetArchitecturePattern;
    private String targetArchitectureDescription;
    private String migrationDiagram;

}
