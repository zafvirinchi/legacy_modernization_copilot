package com.ailegacy.modernization.copilot.application.mappers;

import com.ailegacy.modernization.copilot.domain.entities.ArchitectureAnalysisReport;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.architecture.ArchitectureAnalysisResponse;
import org.springframework.stereotype.Component;

/**
 * Maps {@link ArchitectureAnalysisReport} entities to their read-only response DTO.
 */
@Component
public class ArchitectureAnalysisMapper {

    public ArchitectureAnalysisResponse toResponse(ArchitectureAnalysisReport report) {
        return ArchitectureAnalysisResponse.builder()
                .id(report.getId())
                .projectId(report.getProjectId())
                .detectedPattern(report.getDetectedPattern())
                .currentArchitectureDescription(report.getCurrentArchitectureDescription())
                .currentArchitectureDiagram(report.getCurrentArchitectureDiagram())
                .architectureScore(report.getArchitectureScore())
                .architectureScoreJustification(report.getArchitectureScoreJustification())
                .recommendations(report.getRecommendations())
                .targetArchitecturePattern(report.getTargetArchitecturePattern())
                .targetArchitectureDescription(report.getTargetArchitectureDescription())
                .migrationDiagram(report.getMigrationDiagram())
                .filesAnalyzed(report.getFilesAnalyzed())
                .totalProjectFiles(report.getTotalProjectFiles())
                .createdAt(report.getCreatedAt())
                .build();
    }

}
