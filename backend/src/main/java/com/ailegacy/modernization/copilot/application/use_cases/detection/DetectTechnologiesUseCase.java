package com.ailegacy.modernization.copilot.application.use_cases.detection;

import com.ailegacy.modernization.copilot.application.mappers.TechnologyDetectionMapper;
import com.ailegacy.modernization.copilot.application.use_cases.UseCase;
import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.domain.entities.Project;
import com.ailegacy.modernization.copilot.domain.entities.TechnologyDetectionResult;
import com.ailegacy.modernization.copilot.domain.exceptions.ResourceNotFoundException;
import com.ailegacy.modernization.copilot.domain.repositories.ProjectRepository;
import com.ailegacy.modernization.copilot.domain.repositories.TechnologyDetectionRepository;
import com.ailegacy.modernization.copilot.infrastructure.analysis.TechnologyDetectionEngine;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.detection.TechnologyDetectionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Runs the technology detection agent against an uploaded project and persists
 * the result.
 *
 * This use case intentionally stops once the result is saved - it does not
 * trigger architecture analysis or any later pipeline stage.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DetectTechnologiesUseCase implements UseCase<GetProjectCommand, TechnologyDetectionResponse> {

    private final ProjectRepository projectRepository;
    private final TechnologyDetectionRepository technologyDetectionRepository;
    private final TechnologyDetectionEngine technologyDetectionEngine;
    private final TechnologyDetectionMapper technologyDetectionMapper;

    @Override
    public TechnologyDetectionResponse execute(GetProjectCommand command) {
        Project project = projectRepository.findByIdAndOwnerId(command.projectId(), command.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", command.projectId()));

        TechnologyDetectionResult result = technologyDetectionEngine.detect(project.getId(), project.getStoragePath());

        // Re-running detection replaces the previous result rather than accumulating duplicates.
        technologyDetectionRepository.deleteByProjectId(project.getId());
        TechnologyDetectionResult saved = technologyDetectionRepository.save(result);

        log.info("Technology detection stored | projectId={} | technologiesFound={}",
                project.getId(), saved.getDetectedTechnologies().size());

        return technologyDetectionMapper.toResponse(saved);
    }

}
