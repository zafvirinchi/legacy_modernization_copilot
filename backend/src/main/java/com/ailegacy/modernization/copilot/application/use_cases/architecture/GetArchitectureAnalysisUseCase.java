package com.ailegacy.modernization.copilot.application.use_cases.architecture;

import com.ailegacy.modernization.copilot.application.mappers.ArchitectureAnalysisMapper;
import com.ailegacy.modernization.copilot.application.use_cases.UseCase;
import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.domain.entities.ArchitectureAnalysisReport;
import com.ailegacy.modernization.copilot.domain.exceptions.ResourceNotFoundException;
import com.ailegacy.modernization.copilot.domain.repositories.ArchitectureAnalysisReportRepository;
import com.ailegacy.modernization.copilot.domain.repositories.ProjectRepository;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.architecture.ArchitectureAnalysisResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Retrieves a previously generated architecture analysis report, scoped to
 * its project's owner.
 */
@Component
@RequiredArgsConstructor
public class GetArchitectureAnalysisUseCase implements UseCase<GetProjectCommand, ArchitectureAnalysisResponse> {

    private final ProjectRepository projectRepository;
    private final ArchitectureAnalysisReportRepository architectureAnalysisReportRepository;
    private final ArchitectureAnalysisMapper architectureAnalysisMapper;

    @Override
    public ArchitectureAnalysisResponse execute(GetProjectCommand command) {
        projectRepository.findByIdAndOwnerId(command.projectId(), command.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", command.projectId()));

        ArchitectureAnalysisReport report = architectureAnalysisReportRepository.findByProjectId(command.projectId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No architecture analysis report found for this project. Run analysis first."));

        return architectureAnalysisMapper.toResponse(report);
    }

}
