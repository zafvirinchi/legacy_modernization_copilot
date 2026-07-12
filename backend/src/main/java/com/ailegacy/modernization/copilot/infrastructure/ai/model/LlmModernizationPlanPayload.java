package com.ailegacy.modernization.copilot.infrastructure.ai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Deserialization target for the modernization planner's expected LLM response shape.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LlmModernizationPlanPayload {

    private String migrationStrategy;
    private String estimatedTimeline;
    private String migrationComplexity;
    private List<LlmPriorityMatrixItem> priorityMatrix;
    private List<String> quickWins;
    private List<LlmRisk> risks;
    private List<LlmRequiredTechnology> requiredTechnologies;

}
