package com.ailegacy.modernization.copilot.application.use_cases.analysis;

import com.ailegacy.modernization.copilot.application.mappers.BusinessAnalysisMapper;
import com.ailegacy.modernization.copilot.application.use_cases.UseCase;
import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.domain.entities.BusinessAnalysisReport;
import com.ailegacy.modernization.copilot.domain.exceptions.ResourceNotFoundException;
import com.ailegacy.modernization.copilot.domain.repositories.BusinessAnalysisReportRepository;
import com.ailegacy.modernization.copilot.domain.repositories.ProjectRepository;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.analysis.BusinessAnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Retrieves a previously generated business analysis report, scoped to its
 * project's owner.
 */
@Component
@RequiredArgsConstructor
public class GetBusinessAnalysisUseCase implements UseCase<GetProjectCommand, BusinessAnalysisResponse> {

    private final ProjectRepository projectRepository;
    private final BusinessAnalysisReportRepository businessAnalysisReportRepository;
    private final BusinessAnalysisMapper businessAnalysisMapper;

    @Override
    public BusinessAnalysisResponse execute(GetProjectCommand command) {
        projectRepository.findByIdAndOwnerId(command.projectId(), command.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", command.projectId()));

        BusinessAnalysisReport report = businessAnalysisReportRepository.findByProjectId(command.projectId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No business analysis report found for this project. Run analysis first."));

        return businessAnalysisMapper.toResponse(report);
    }

}
