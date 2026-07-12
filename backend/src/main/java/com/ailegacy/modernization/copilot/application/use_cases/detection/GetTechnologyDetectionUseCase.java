package com.ailegacy.modernization.copilot.application.use_cases.detection;

import com.ailegacy.modernization.copilot.application.mappers.TechnologyDetectionMapper;
import com.ailegacy.modernization.copilot.application.use_cases.UseCase;
import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.domain.entities.TechnologyDetectionResult;
import com.ailegacy.modernization.copilot.domain.exceptions.ResourceNotFoundException;
import com.ailegacy.modernization.copilot.domain.repositories.ProjectRepository;
import com.ailegacy.modernization.copilot.domain.repositories.TechnologyDetectionRepository;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.detection.TechnologyDetectionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Retrieves a previously computed technology detection result, scoped to its
 * project's owner.
 */
@Component
@RequiredArgsConstructor
public class GetTechnologyDetectionUseCase implements UseCase<GetProjectCommand, TechnologyDetectionResponse> {

    private final ProjectRepository projectRepository;
    private final TechnologyDetectionRepository technologyDetectionRepository;
    private final TechnologyDetectionMapper technologyDetectionMapper;

    @Override
    public TechnologyDetectionResponse execute(GetProjectCommand command) {
        projectRepository.findByIdAndOwnerId(command.projectId(), command.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", command.projectId()));

        TechnologyDetectionResult result = technologyDetectionRepository.findByProjectId(command.projectId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No technology detection result found for this project. Run detection first."));

        return technologyDetectionMapper.toResponse(result);
    }

}
