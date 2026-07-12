package com.ailegacy.modernization.copilot.application.use_cases.generator;

import com.ailegacy.modernization.copilot.application.mappers.GeneratedSpringBootCodeMapper;
import com.ailegacy.modernization.copilot.application.use_cases.UseCase;
import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.domain.entities.GeneratedSpringBootCode;
import com.ailegacy.modernization.copilot.domain.entities.Project;
import com.ailegacy.modernization.copilot.domain.entities.TechnologyDetectionResult;
import com.ailegacy.modernization.copilot.domain.exceptions.ResourceNotFoundException;
import com.ailegacy.modernization.copilot.domain.repositories.GeneratedSpringBootCodeRepository;
import com.ailegacy.modernization.copilot.domain.repositories.ProjectRepository;
import com.ailegacy.modernization.copilot.domain.repositories.TechnologyDetectionRepository;
import com.ailegacy.modernization.copilot.infrastructure.ai.SpringBootGenerator;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.generator.GeneratedSpringBootCodeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Runs the Spring Boot generator against an uploaded project and persists the
 * resulting representative example.
 *
 * If a technology detection result already exists for this project, its
 * findings are folded into the prompt as extra context; it is not required
 * for generation to run standalone.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GenerateSpringBootCodeUseCase implements UseCase<GetProjectCommand, GeneratedSpringBootCodeResponse> {

    private final ProjectRepository projectRepository;
    private final GeneratedSpringBootCodeRepository generatedSpringBootCodeRepository;
    private final TechnologyDetectionRepository technologyDetectionRepository;
    private final SpringBootGenerator springBootGenerator;
    private final GeneratedSpringBootCodeMapper generatedSpringBootCodeMapper;

    @Override
    public GeneratedSpringBootCodeResponse execute(GetProjectCommand command) {
        Project project = projectRepository.findByIdAndOwnerId(command.projectId(), command.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", command.projectId()));

        List<String> knownTechnologies = technologyDetectionRepository.findByProjectId(project.getId())
                .map(TechnologyDetectionResult::getDetectedTechnologies)
                .map(technologies -> technologies.stream().map(t -> t.getTechnology().name()).toList())
                .orElse(List.of());

        GeneratedSpringBootCode result = springBootGenerator.generate(
                project.getId(), project.getName(), project.getStoragePath(), knownTechnologies);

        // Re-running generation replaces the previous result rather than accumulating duplicates.
        generatedSpringBootCodeRepository.deleteByProjectId(project.getId());
        GeneratedSpringBootCode saved = generatedSpringBootCodeRepository.save(result);

        log.info("Spring Boot code generation stored | projectId={}", project.getId());
        return generatedSpringBootCodeMapper.toResponse(saved);
    }

}
