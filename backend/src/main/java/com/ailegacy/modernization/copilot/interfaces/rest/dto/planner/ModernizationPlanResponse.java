package com.ailegacy.modernization.copilot.interfaces.rest.dto.planner;

import com.ailegacy.modernization.copilot.domain.enums.Level;
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
public class ModernizationPlanResponse {

    private String id;
    private String projectId;
    private String migrationStrategy;
    private String estimatedTimeline;
    private Level migrationComplexity;
    private List<PriorityMatrixItemResponse> priorityMatrix;
    private List<String> quickWins;
    private List<RiskResponse> risks;
    private List<RequiredTechnologyResponse> requiredTechnologies;
    private int filesAnalyzed;
    private int totalProjectFiles;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Instant createdAt;

}
