package com.ailegacy.modernization.copilot.application.use_cases.security;

import com.ailegacy.modernization.copilot.application.mappers.SecurityAnalysisMapper;
import com.ailegacy.modernization.copilot.application.use_cases.UseCase;
import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.domain.entities.SecurityAnalysisReport;
import com.ailegacy.modernization.copilot.domain.exceptions.ResourceNotFoundException;
import com.ailegacy.modernization.copilot.domain.repositories.ProjectRepository;
import com.ailegacy.modernization.copilot.domain.repositories.SecurityAnalysisReportRepository;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.security.SecurityAnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Retrieves a previously generated security analysis report, scoped to its
 * project's owner.
 */
@Component
@RequiredArgsConstructor
public class GetSecurityAnalysisUseCase implements UseCase<GetProjectCommand, SecurityAnalysisResponse> {

    private final ProjectRepository projectRepository;
    private final SecurityAnalysisReportRepository securityAnalysisReportRepository;
    private final SecurityAnalysisMapper securityAnalysisMapper;

    @Override
    public SecurityAnalysisResponse execute(GetProjectCommand command) {
        projectRepository.findByIdAndOwnerId(command.projectId(), command.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", command.projectId()));

        SecurityAnalysisReport report = securityAnalysisReportRepository.findByProjectId(command.projectId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No security analysis report found for this project. Run analysis first."));

        return securityAnalysisMapper.toResponse(report);
    }

}
