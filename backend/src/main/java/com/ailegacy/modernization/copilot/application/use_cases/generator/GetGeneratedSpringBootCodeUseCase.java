package com.ailegacy.modernization.copilot.application.use_cases.generator;

import com.ailegacy.modernization.copilot.application.mappers.GeneratedSpringBootCodeMapper;
import com.ailegacy.modernization.copilot.application.use_cases.UseCase;
import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.domain.entities.GeneratedSpringBootCode;
import com.ailegacy.modernization.copilot.domain.exceptions.ResourceNotFoundException;
import com.ailegacy.modernization.copilot.domain.repositories.GeneratedSpringBootCodeRepository;
import com.ailegacy.modernization.copilot.domain.repositories.ProjectRepository;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.generator.GeneratedSpringBootCodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Retrieves a previously generated representative Spring Boot example, scoped
 * to its project's owner.
 */
@Component
@RequiredArgsConstructor
public class GetGeneratedSpringBootCodeUseCase implements UseCase<GetProjectCommand, GeneratedSpringBootCodeResponse> {

    private final ProjectRepository projectRepository;
    private final GeneratedSpringBootCodeRepository generatedSpringBootCodeRepository;
    private final GeneratedSpringBootCodeMapper generatedSpringBootCodeMapper;

    @Override
    public GeneratedSpringBootCodeResponse execute(GetProjectCommand command) {
        projectRepository.findByIdAndOwnerId(command.projectId(), command.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", command.projectId()));

        GeneratedSpringBootCode code = generatedSpringBootCodeRepository.findByProjectId(command.projectId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No generated Spring Boot code found for this project. Generate it first."));

        return generatedSpringBootCodeMapper.toResponse(code);
    }

}
