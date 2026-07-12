package com.ailegacy.modernization.copilot.application.use_cases.architecture;

import com.ailegacy.modernization.copilot.application.mappers.ArchitectureAnalysisMapper;
import com.ailegacy.modernization.copilot.application.use_cases.UseCase;
import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.domain.entities.ArchitectureAnalysisReport;
import com.ailegacy.modernization.copilot.domain.entities.BusinessAnalysisReport;
import com.ailegacy.modernization.copilot.domain.entities.Project;
import com.ailegacy.modernization.copilot.domain.entities.TechnologyDetectionResult;
import com.ailegacy.modernization.copilot.domain.exceptions.ResourceNotFoundException;
import com.ailegacy.modernization.copilot.domain.repositories.ArchitectureAnalysisReportRepository;
import com.ailegacy.modernization.copilot.domain.repositories.BusinessAnalysisReportRepository;
import com.ailegacy.modernization.copilot.domain.repositories.ProjectRepository;
import com.ailegacy.modernization.copilot.domain.repositories.TechnologyDetectionRepository;
import com.ailegacy.modernization.copilot.infrastructure.ai.ArchitectureAnalyzer;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.architecture.ArchitectureAnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Runs the architecture analyzer against an uploaded project and persists the
 * resulting report.
 *
 * If a technology detection result or business analysis report already exists
 * for this project, their findings are folded into the prompt as extra
 * context; neither is required for architecture analysis to run standalone.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyzeArchitectureUseCase implements UseCase<GetProjectCommand, ArchitectureAnalysisResponse> {

    private final ProjectRepository projectRepository;
    private final ArchitectureAnalysisReportRepository architectureAnalysisReportRepository;
    private final TechnologyDetectionRepository technologyDetectionRepository;
    private final BusinessAnalysisReportRepository businessAnalysisReportRepository;
    private final ArchitectureAnalyzer architectureAnalyzer;
    private final ArchitectureAnalysisMapper architectureAnalysisMapper;

    @Override
    public ArchitectureAnalysisResponse execute(GetProjectCommand command) {
        Project project = projectRepository.findByIdAndOwnerId(command.projectId(), command.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", command.projectId()));

        List<String> knownTechnologies = technologyDetectionRepository.findByProjectId(project.getId())
                .map(TechnologyDetectionResult::getDetectedTechnologies)
                .map(technologies -> technologies.stream().map(t -> t.getTechnology().name()).toList())
                .orElse(List.of());

        String businessContext = businessAnalysisReportRepository.findByProjectId(project.getId())
                .map(BusinessAnalysisReport::getBusinessSummary)
                .orElse(null);

        ArchitectureAnalysisReport report = architectureAnalyzer.analyze(
                project.getId(), project.getName(), project.getStoragePath(), knownTechnologies, businessContext);

        // Re-running analysis replaces the previous report rather than accumulating duplicates.
        architectureAnalysisReportRepository.deleteByProjectId(project.getId());
        ArchitectureAnalysisReport saved = architectureAnalysisReportRepository.save(report);

        log.info("Architecture analysis stored | projectId={} | pattern={}", project.getId(), saved.getDetectedPattern());
        return architectureAnalysisMapper.toResponse(saved);
    }

}
