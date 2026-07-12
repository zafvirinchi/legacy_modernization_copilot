package com.ailegacy.modernization.copilot.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A plain-English description of one module identified in the analyzed project.
 * Embedded within {@link BusinessAnalysisReport}, not persisted independently.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleSummary {

    private String moduleName;

    private String description;

}
