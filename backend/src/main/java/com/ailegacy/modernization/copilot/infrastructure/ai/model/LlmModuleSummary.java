package com.ailegacy.modernization.copilot.infrastructure.ai.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Deserialization target for one entry of the LLM's "moduleSummary" array.
 */
@Data
@NoArgsConstructor
public class LlmModuleSummary {

    private String moduleName;
    private String description;

}
