package com.ailegacy.modernization.copilot.application.use_cases.performance;

import com.ailegacy.modernization.copilot.application.mappers.PerformanceAnalysisMapper;
import com.ailegacy.modernization.copilot.application.use_cases.UseCase;
import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.domain.entities.PerformanceAnalysisReport;
import com.ailegacy.modernization.copilot.domain.entities.Project;
import com.ailegacy.modernization.copilot.domain.entities.TechnologyDetectionResult;
import com.ailegacy.modernization.copilot.domain.exceptions.ResourceNotFoundException;
import com.ailegacy.modernization.copilot.domain.repositories.PerformanceAnalysisReportRepository;
import com.ailegacy.modernization.copilot.domain.repositories.ProjectRepository;
import com.ailegacy.modernization.copilot.domain.repositories.TechnologyDetectionRepository;
import com.ailegacy.modernization.copilot.infrastructure.ai.PerformanceAnalyzer;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.performance.PerformanceAnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Runs the performance analyzer against an uploaded project and persists the
 * resulting report.
 *
 * If a technology detection result already exists for this project, its
 * findings are folded into the prompt as extra context; it is not required
 * for performance analysis to run standalone.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyzePerformanceUseCase implements UseCase<GetProjectCommand, PerformanceAnalysisResponse> {

    private final ProjectRepository projectRepository;
    private final PerformanceAnalysisReportRepository performanceAnalysisReportRepository;
    private final TechnologyDetectionRepository technologyDetectionRepository;
    private final PerformanceAnalyzer performanceAnalyzer;
    private final PerformanceAnalysisMapper performanceAnalysisMapper;

    @Override
    public PerformanceAnalysisResponse execute(GetProjectCommand command) {
        Project project = projectRepository.findByIdAndOwnerId(command.projectId(), command.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", command.projectId()));

        List<String> knownTechnologies = technologyDetectionRepository.findByProjectId(project.getId())
                .map(TechnologyDetectionResult::getDetectedTechnologies)
                .map(technologies -> technologies.stream().map(t -> t.getTechnology().name()).toList())
                .orElse(List.of());

        PerformanceAnalysisReport report = performanceAnalyzer.analyze(
                project.getId(), project.getName(), project.getStoragePath(), knownTechnologies);

        // Re-running analysis replaces the previous report rather than accumulating duplicates.
        performanceAnalysisReportRepository.deleteByProjectId(project.getId());
        PerformanceAnalysisReport saved = performanceAnalysisReportRepository.save(report);

        log.info("Performance analysis stored | projectId={} | findings={} | performanceScore={}",
                project.getId(), saved.getFindings().size(), saved.getPerformanceScore());
        return performanceAnalysisMapper.toResponse(saved);
    }

}
