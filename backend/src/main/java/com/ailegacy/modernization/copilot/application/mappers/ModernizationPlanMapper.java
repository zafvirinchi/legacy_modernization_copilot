package com.ailegacy.modernization.copilot.application.mappers;

import com.ailegacy.modernization.copilot.domain.entities.ModernizationPlan;
import com.ailegacy.modernization.copilot.domain.entities.PriorityMatrixItem;
import com.ailegacy.modernization.copilot.domain.entities.RequiredTechnology;
import com.ailegacy.modernization.copilot.domain.entities.Risk;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.planner.ModernizationPlanResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.planner.PriorityMatrixItemResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.planner.RequiredTechnologyResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.planner.RiskResponse;
import org.springframework.stereotype.Component;

/**
 * Maps {@link ModernizationPlan} entities to their read-only response DTO.
 */
@Component
public class ModernizationPlanMapper {

    public ModernizationPlanResponse toResponse(ModernizationPlan plan) {
        return ModernizationPlanResponse.builder()
                .id(plan.getId())
                .projectId(plan.getProjectId())
                .migrationStrategy(plan.getMigrationStrategy())
                .estimatedTimeline(plan.getEstimatedTimeline())
                .migrationComplexity(plan.getMigrationComplexity())
                .priorityMatrix(plan.getPriorityMatrix().stream().map(this::toResponse).toList())
                .quickWins(plan.getQuickWins())
                .risks(plan.getRisks().stream().map(this::toResponse).toList())
                .requiredTechnologies(plan.getRequiredTechnologies().stream().map(this::toResponse).toList())
                .filesAnalyzed(plan.getFilesAnalyzed())
                .totalProjectFiles(plan.getTotalProjectFiles())
                .createdAt(plan.getCreatedAt())
                .build();
    }

    private PriorityMatrixItemResponse toResponse(PriorityMatrixItem item) {
        return PriorityMatrixItemResponse.builder()
                .item(item.getItem())
                .impact(item.getImpact())
                .effort(item.getEffort())
                .build();
    }

    private RiskResponse toResponse(Risk risk) {
        return RiskResponse.builder()
                .description(risk.getDescription())
                .severity(risk.getSeverity())
                .build();
    }

    private RequiredTechnologyResponse toResponse(RequiredTechnology technology) {
        return RequiredTechnologyResponse.builder()
                .technology(technology.getTechnology())
                .recommended(technology.isRecommended())
                .reason(technology.getReason())
                .build();
    }

}
