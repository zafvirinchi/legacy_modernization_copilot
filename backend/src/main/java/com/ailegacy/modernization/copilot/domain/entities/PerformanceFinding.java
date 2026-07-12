package com.ailegacy.modernization.copilot.domain.entities;

import com.ailegacy.modernization.copilot.domain.enums.PerformanceIssueType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * A single detected performance/code-quality issue with its optimization
 * suggestion and a modern alternative. Embedded within
 * {@link PerformanceAnalysisReport}, not persisted independently.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceFinding {

    private PerformanceIssueType issueType;

    private String title;

    private String description;

    private String location;

    private String optimizationSuggestion;

    private String modernAlternative;

    private List<String> evidence;

}
