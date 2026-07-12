package com.ailegacy.modernization.copilot.application.use_cases.project;

import com.ailegacy.modernization.copilot.application.mappers.ProjectSummaryMapper;
import com.ailegacy.modernization.copilot.application.use_cases.UseCase;
import com.ailegacy.modernization.copilot.domain.entities.Project;
import com.ailegacy.modernization.copilot.domain.exceptions.ResourceNotFoundException;
import com.ailegacy.modernization.copilot.domain.repositories.ProjectRepository;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.project.ProjectSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Retrieves a single project's summary, scoped to its owner.
 */
@Component
@RequiredArgsConstructor
public class GetProjectUseCase implements UseCase<GetProjectCommand, ProjectSummaryResponse> {

    private final ProjectRepository projectRepository;
    private final ProjectSummaryMapper projectSummaryMapper;

    @Override
    public ProjectSummaryResponse execute(GetProjectCommand command) {
        Project project = projectRepository.findByIdAndOwnerId(command.projectId(), command.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", command.projectId()));
        return projectSummaryMapper.toSummaryResponse(project);
    }

}
