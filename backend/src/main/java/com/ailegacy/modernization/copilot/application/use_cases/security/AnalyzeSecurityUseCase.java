package com.ailegacy.modernization.copilot.application.use_cases.security;

import com.ailegacy.modernization.copilot.application.mappers.SecurityAnalysisMapper;
import com.ailegacy.modernization.copilot.application.use_cases.UseCase;
import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.domain.entities.Project;
import com.ailegacy.modernization.copilot.domain.entities.SecurityAnalysisReport;
import com.ailegacy.modernization.copilot.domain.entities.TechnologyDetectionResult;
import com.ailegacy.modernization.copilot.domain.exceptions.ResourceNotFoundException;
import com.ailegacy.modernization.copilot.domain.repositories.ProjectRepository;
import com.ailegacy.modernization.copilot.domain.repositories.SecurityAnalysisReportRepository;
import com.ailegacy.modernization.copilot.domain.repositories.TechnologyDetectionRepository;
import com.ailegacy.modernization.copilot.infrastructure.ai.SecurityAnalyzer;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.security.SecurityAnalysisResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Runs the security analyzer against an uploaded project and persists the
 * resulting report.
 *
 * If a technology detection result already exists for this project, its
 * findings are folded into the prompt as extra context; it is not required
 * for security analysis to run standalone.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyzeSecurityUseCase implements UseCase<GetProjectCommand, SecurityAnalysisResponse> {

    private final ProjectRepository projectRepository;
    private final SecurityAnalysisReportRepository securityAnalysisReportRepository;
    private final TechnologyDetectionRepository technologyDetectionRepository;
    private final SecurityAnalyzer securityAnalyzer;
    private final SecurityAnalysisMapper securityAnalysisMapper;

    @Override
    public SecurityAnalysisResponse execute(GetProjectCommand command) {
        Project project = projectRepository.findByIdAndOwnerId(command.projectId(), command.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", command.projectId()));

        List<String> knownTechnologies = technologyDetectionRepository.findByProjectId(project.getId())
                .map(TechnologyDetectionResult::getDetectedTechnologies)
                .map(technologies -> technologies.stream().map(t -> t.getTechnology().name()).toList())
                .orElse(List.of());

        SecurityAnalysisReport report = securityAnalyzer.analyze(
                project.getId(), project.getName(), project.getStoragePath(), knownTechnologies);

        // Re-running analysis replaces the previous report rather than accumulating duplicates.
        securityAnalysisReportRepository.deleteByProjectId(project.getId());
        SecurityAnalysisReport saved = securityAnalysisReportRepository.save(report);

        log.info("Security analysis stored | projectId={} | findings={} | overallRiskScore={}",
                project.getId(), saved.getFindings().size(), saved.getOverallRiskScore());
        return securityAnalysisMapper.toResponse(saved);
    }

}
