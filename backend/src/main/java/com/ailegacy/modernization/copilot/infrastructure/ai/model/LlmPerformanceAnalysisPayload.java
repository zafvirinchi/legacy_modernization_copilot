package com.ailegacy.modernization.copilot.infrastructure.ai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Deserialization target for the performance analyzer's expected LLM response shape.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LlmPerformanceAnalysisPayload {

    private Integer performanceScore;
    private String performanceScoreJustification;
    private List<LlmPerformanceFinding> findings;

}
