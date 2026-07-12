package com.ailegacy.modernization.copilot.application.mappers;

import com.ailegacy.modernization.copilot.domain.entities.DetectedTechnology;
import com.ailegacy.modernization.copilot.domain.entities.TechnologyDetectionResult;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.detection.DetectedTechnologyResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.detection.TechnologyDetectionResponse;
import org.springframework.stereotype.Component;

/**
 * Maps {@link TechnologyDetectionResult} entities to their read-only response DTO.
 */
@Component
public class TechnologyDetectionMapper {

    public TechnologyDetectionResponse toResponse(TechnologyDetectionResult result) {
        return TechnologyDetectionResponse.builder()
                .id(result.getId())
                .projectId(result.getProjectId())
                .detectedTechnologies(result.getDetectedTechnologies().stream()
                        .map(this::toResponse)
                        .toList())
                .javaVersion(result.getJavaVersion())
                .databases(result.getDatabases())
                .buildTool(result.getBuildTool())
                .applicationServer(result.getApplicationServer())
                .createdAt(result.getCreatedAt())
                .build();
    }

    private DetectedTechnologyResponse toResponse(DetectedTechnology detected) {
        return DetectedTechnologyResponse.builder()
                .technology(detected.getTechnology())
                .confidenceScore(detected.getConfidenceScore())
                .evidence(detected.getEvidence())
                .build();
    }

}
