package com.ailegacy.modernization.copilot.interfaces.rest.dto.architecture;

import com.ailegacy.modernization.copilot.domain.enums.ArchitecturePattern;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchitectureAnalysisResponse {

    private String id;
    private String projectId;
    private ArchitecturePattern detectedPattern;
    private String currentArchitectureDescription;
    private String currentArchitectureDiagram;
    private int architectureScore;
    private String architectureScoreJustification;
    private List<String> recommendations;
    private ArchitecturePattern targetArchitecturePattern;
    private String targetArchitectureDescription;
    private String migrationDiagram;
    private int filesAnalyzed;
    private int totalProjectFiles;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant createdAt;

}
