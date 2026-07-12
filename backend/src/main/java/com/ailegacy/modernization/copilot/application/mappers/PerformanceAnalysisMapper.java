package com.ailegacy.modernization.copilot.application.mappers;

import com.ailegacy.modernization.copilot.domain.entities.PerformanceAnalysisReport;
import com.ailegacy.modernization.copilot.domain.entities.PerformanceFinding;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.performance.PerformanceAnalysisResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.performance.PerformanceFindingResponse;
import org.springframework.stereotype.Component;

/**
 * Maps {@link PerformanceAnalysisReport} entities to their read-only response DTO.
 */
@Component
public class PerformanceAnalysisMapper {

    public PerformanceAnalysisResponse toResponse(PerformanceAnalysisReport report) {
        return PerformanceAnalysisResponse.builder()
                .id(report.getId())
                .projectId(report.getProjectId())
                .performanceScore(report.getPerformanceScore())
                .performanceScoreJustification(report.getPerformanceScoreJustification())
                .findings(report.getFindings().stream()
                        .map(this::toResponse)
                        .toList())
                .filesAnalyzed(report.getFilesAnalyzed())
                .totalProjectFiles(report.getTotalProjectFiles())
                .createdAt(report.getCreatedAt())
                .build();
    }

    private PerformanceFindingResponse toResponse(PerformanceFinding finding) {
        return PerformanceFindingResponse.builder()
                .issueType(finding.getIssueType())
                .title(finding.getTitle())
                .description(finding.getDescription())
                .location(finding.getLocation())
                .optimizationSuggestion(finding.getOptimizationSuggestion())
                .modernAlternative(finding.getModernAlternative())
                .evidence(finding.getEvidence())
                .build();
    }

}
