package com.ailegacy.modernization.copilot.application.mappers;

import com.ailegacy.modernization.copilot.domain.entities.SecurityAnalysisReport;
import com.ailegacy.modernization.copilot.domain.entities.SecurityFinding;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.security.SecurityAnalysisResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.security.SecurityFindingResponse;
import org.springframework.stereotype.Component;

/**
 * Maps {@link SecurityAnalysisReport} entities to their read-only response DTO.
 */
@Component
public class SecurityAnalysisMapper {

    public SecurityAnalysisResponse toResponse(SecurityAnalysisReport report) {
        return SecurityAnalysisResponse.builder()
                .id(report.getId())
                .projectId(report.getProjectId())
                .findings(report.getFindings().stream()
                        .map(this::toResponse)
                        .toList())
                .overallRiskScore(report.getOverallRiskScore())
                .filesAnalyzed(report.getFilesAnalyzed())
                .totalProjectFiles(report.getTotalProjectFiles())
                .createdAt(report.getCreatedAt())
                .build();
    }

    private SecurityFindingResponse toResponse(SecurityFinding finding) {
        return SecurityFindingResponse.builder()
                .issueType(finding.getIssueType())
                .title(finding.getTitle())
                .description(finding.getDescription())
                .severity(finding.getSeverity())
                .riskScore(finding.getRiskScore())
                .location(finding.getLocation())
                .recommendation(finding.getRecommendation())
                .modernAlternative(finding.getModernAlternative())
                .evidence(finding.getEvidence())
                .build();
    }

}
