package com.ailegacy.modernization.copilot.infrastructure.report.model;

import com.ailegacy.modernization.copilot.domain.entities.ArchitectureAnalysisReport;
import com.ailegacy.modernization.copilot.domain.entities.BusinessAnalysisReport;
import com.ailegacy.modernization.copilot.domain.entities.GeneratedSpringBootCode;
import com.ailegacy.modernization.copilot.domain.entities.ModernizationPlan;
import com.ailegacy.modernization.copilot.domain.entities.PerformanceAnalysisReport;
import com.ailegacy.modernization.copilot.domain.entities.Project;
import com.ailegacy.modernization.copilot.domain.entities.SecurityAnalysisReport;
import com.ailegacy.modernization.copilot.domain.entities.TechnologyDetectionResult;

/**
 * Aggregates a project with every prior analysis available for it. Each
 * analysis field is null when that stage hasn't been run yet - the report
 * renders a placeholder for any missing section rather than failing.
 */
public record ModernizationReportData(
        Project project,
        TechnologyDetectionResult technologyDetection,
        BusinessAnalysisReport businessAnalysis,
        ArchitectureAnalysisReport architectureAnalysis,
        SecurityAnalysisReport securityAnalysis,
        PerformanceAnalysisReport performanceAnalysis,
        ModernizationPlan modernizationPlan,
        GeneratedSpringBootCode generatedSpringBootCode
) {
}
