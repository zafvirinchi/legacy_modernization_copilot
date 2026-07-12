package com.ailegacy.modernization.copilot.application.use_cases.report;

import com.ailegacy.modernization.copilot.application.use_cases.UseCase;
import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.domain.entities.Project;
import com.ailegacy.modernization.copilot.domain.exceptions.ResourceNotFoundException;
import com.ailegacy.modernization.copilot.domain.repositories.ArchitectureAnalysisReportRepository;
import com.ailegacy.modernization.copilot.domain.repositories.BusinessAnalysisReportRepository;
import com.ailegacy.modernization.copilot.domain.repositories.GeneratedSpringBootCodeRepository;
import com.ailegacy.modernization.copilot.domain.repositories.ModernizationPlanRepository;
import com.ailegacy.modernization.copilot.domain.repositories.PerformanceAnalysisReportRepository;
import com.ailegacy.modernization.copilot.domain.repositories.ProjectRepository;
import com.ailegacy.modernization.copilot.domain.repositories.SecurityAnalysisReportRepository;
import com.ailegacy.modernization.copilot.domain.repositories.TechnologyDetectionRepository;
import com.ailegacy.modernization.copilot.infrastructure.report.ModernizationReportPdfGenerator;
import com.ailegacy.modernization.copilot.infrastructure.report.model.ModernizationReportData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Compiles an Enterprise Modernization Report PDF for an uploaded project,
 * combining every prior analysis available for it. None of the underlying
 * analyses are required to have been run - any missing section renders a
 * placeholder in the PDF instead.
 *
 * Unlike the other agents, this performs no new AI generation and persists
 * nothing; the report is assembled fresh from already-stored analyses on
 * every request.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GenerateModernizationReportUseCase implements UseCase<GetProjectCommand, GeneratedReportFile> {

    private final ProjectRepository projectRepository;
    private final TechnologyDetectionRepository technologyDetectionRepository;
    private final BusinessAnalysisReportRepository businessAnalysisReportRepository;
    private final ArchitectureAnalysisReportRepository architectureAnalysisReportRepository;
    private final SecurityAnalysisReportRepository securityAnalysisReportRepository;
    private final PerformanceAnalysisReportRepository performanceAnalysisReportRepository;
    private final ModernizationPlanRepository modernizationPlanRepository;
    private final GeneratedSpringBootCodeRepository generatedSpringBootCodeRepository;
    private final ModernizationReportPdfGenerator modernizationReportPdfGenerator;

    @Override
    public GeneratedReportFile execute(GetProjectCommand command) {
        Project project = projectRepository.findByIdAndOwnerId(command.projectId(), command.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", command.projectId()));

        ModernizationReportData data = new ModernizationReportData(
                project,
                technologyDetectionRepository.findByProjectId(project.getId()).orElse(null),
                businessAnalysisReportRepository.findByProjectId(project.getId()).orElse(null),
                architectureAnalysisReportRepository.findByProjectId(project.getId()).orElse(null),
                securityAnalysisReportRepository.findByProjectId(project.getId()).orElse(null),
                performanceAnalysisReportRepository.findByProjectId(project.getId()).orElse(null),
                modernizationPlanRepository.findByProjectId(project.getId()).orElse(null),
                generatedSpringBootCodeRepository.findByProjectId(project.getId()).orElse(null)
        );

        log.info("Generating modernization report PDF | projectId={}", project.getId());
        byte[] pdf = modernizationReportPdfGenerator.generate(data);
        return new GeneratedReportFile(pdf, sanitizeFilename(project.getName()) + "-modernization-report.pdf");
    }

    private String sanitizeFilename(String name) {
        String sanitized = name.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9-]+", "-").replaceAll("-{2,}", "-");
        return sanitized.isBlank() ? "project" : sanitized;
    }

}
