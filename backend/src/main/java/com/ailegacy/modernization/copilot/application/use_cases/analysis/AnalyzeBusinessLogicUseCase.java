package com.ailegacy.modernization.copilot.application.use_cases.analysis;

import com.ailegacy.modernization.copilot.application.mappers.BusinessAnalysisMapper;
import com.ailegacy.modernization.copilot.application.use_cases.UseCase;
import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.domain.entities.BusinessAnalysisReport;
import com.ailegacy.modernization.copilot.domain.entities.Project;
import com.ailegacy.modernization.copilot.domain.exceptions.ResourceNotFoundException;
import com.ailegacy.modernization.copilot.domain.repositories.BusinessAnalysisReportRepository;
import com.ailegacy.modernization.copilot.domain.repositories.ProjectRepository;
import com.ailegacy.modernization.copilot.infrastructure.ai.BusinessLogicAnalyzer;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.analysis.BusinessAnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Runs the business logic analyzer against an uploaded project and persists
 * the resulting report.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyzeBusinessLogicUseCase implements UseCase<GetProjectCommand, BusinessAnalysisResponse> {

    private final ProjectRepository projectRepository;
    private final BusinessAnalysisReportRepository businessAnalysisReportRepository;
    private final BusinessLogicAnalyzer businessLogicAnalyzer;
    private final BusinessAnalysisMapper businessAnalysisMapper;

    @Override
    public BusinessAnalysisResponse execute(GetProjectCommand command) {
        Project project = projectRepository.findByIdAndOwnerId(command.projectId(), command.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", command.projectId()));

        BusinessAnalysisReport report = businessLogicAnalyzer.analyze(project.getId(), project.getName(), project.getStoragePath());

        // Re-running analysis replaces the previous report rather than accumulating duplicates.
        businessAnalysisReportRepository.deleteByProjectId(project.getId());
        BusinessAnalysisReport saved = businessAnalysisReportRepository.save(report);

        log.info("Business analysis stored | projectId={}", project.getId());
        return businessAnalysisMapper.toResponse(saved);
    }

}
