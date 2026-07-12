package com.ailegacy.modernization.copilot.application.mappers;

import com.ailegacy.modernization.copilot.domain.entities.BusinessAnalysisReport;
import com.ailegacy.modernization.copilot.domain.entities.ModuleSummary;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.analysis.BusinessAnalysisResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.analysis.ModuleSummaryResponse;
import org.springframework.stereotype.Component;

/**
 * Maps {@link BusinessAnalysisReport} entities to their read-only response DTO.
 */
@Component
public class BusinessAnalysisMapper {

    public BusinessAnalysisResponse toResponse(BusinessAnalysisReport report) {
        return BusinessAnalysisResponse.builder()
                .id(report.getId())
                .projectId(report.getProjectId())
                .businessPurpose(report.getBusinessPurpose())
                .mainModules(report.getMainModules())
                .criticalWorkflows(report.getCriticalWorkflows())
                .coreEntities(report.getCoreEntities())
                .executiveSummary(report.getExecutiveSummary())
                .businessSummary(report.getBusinessSummary())
                .moduleSummary(report.getModuleSummary().stream()
                        .map(this::toResponse)
                        .toList())
                .filesAnalyzed(report.getFilesAnalyzed())
                .totalProjectFiles(report.getTotalProjectFiles())
                .createdAt(report.getCreatedAt())
                .build();
    }

    private ModuleSummaryResponse toResponse(ModuleSummary moduleSummary) {
        return ModuleSummaryResponse.builder()
                .moduleName(moduleSummary.getModuleName())
                .description(moduleSummary.getDescription())
                .build();
    }

}
