package com.ailegacy.modernization.copilot.infrastructure.ai.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Deserialization target for the business logic analyzer's expected LLM
 * response shape. Unknown extra keys are ignored rather than failing parsing,
 * since prompt adherence from an LLM can't be guaranteed to be exact.
 */
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LlmBusinessAnalysisPayload {

    private String businessPurpose;
    private String executiveSummary;
    private String businessSummary;
    private List<String> mainModules;
    private List<String> criticalWorkflows;
    private List<String> coreEntities;
    private List<LlmModuleSummary> moduleSummary;

}
