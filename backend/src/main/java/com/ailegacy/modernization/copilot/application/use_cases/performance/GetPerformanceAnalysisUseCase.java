package com.ailegacy.modernization.copilot.application.use_cases.performance;

import com.ailegacy.modernization.copilot.application.mappers.PerformanceAnalysisMapper;
import com.ailegacy.modernization.copilot.application.use_cases.UseCase;
import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.domain.entities.PerformanceAnalysisReport;
import com.ailegacy.modernization.copilot.domain.exceptions.ResourceNotFoundException;
import com.ailegacy.modernization.copilot.domain.repositories.PerformanceAnalysisReportRepository;
import com.ailegacy.modernization.copilot.domain.repositories.ProjectRepository;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.performance.PerformanceAnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Retrieves a previously generated performance analysis report, scoped to its
 * project's owner.
 */
@Component
@RequiredArgsConstructor
public class GetPerformanceAnalysisUseCase implements UseCase<GetProjectCommand, PerformanceAnalysisResponse> {

    private final ProjectRepository projectRepository;
    private final PerformanceAnalysisReportRepository performanceAnalysisReportRepository;
    private final PerformanceAnalysisMapper performanceAnalysisMapper;

    @Override
    public PerformanceAnalysisResponse execute(GetProjectCommand command) {
        projectRepository.findByIdAndOwnerId(command.projectId(), command.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", command.projectId()));

        PerformanceAnalysisReport report = performanceAnalysisReportRepository.findByProjectId(command.projectId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No performance analysis report found for this project. Run analysis first."));

        return performanceAnalysisMapper.toResponse(report);
    }

}
