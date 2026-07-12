package com.ailegacy.modernization.copilot.interfaces.rest.controllers;

import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.application.use_cases.report.GenerateModernizationReportUseCase;
import com.ailegacy.modernization.copilot.application.use_cases.report.GeneratedReportFile;
import com.ailegacy.modernization.copilot.infrastructure.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Enterprise Modernization Report endpoint.
 *
 * Compiles a downloadable PDF combining every prior analysis available for a
 * project (technology detection, business, architecture, security,
 * performance, migration plan, generated Spring Boot sample). This performs
 * no new AI generation - it is a read-only export of already-stored results.
 */
@RestController
@RequestMapping("/projects/{projectId}/modernization-report")
@RequiredArgsConstructor
@Tag(name = "Modernization Report", description = "Downloadable PDF combining all prior analyses for a project")
public class ModernizationReportController {

    private final GenerateModernizationReportUseCase generateModernizationReportUseCase;

    @GetMapping
    @Operation(
            summary = "Download the enterprise modernization report",
            description = "Compiles a PDF covering executive summary, technology stack, architecture/security/performance review, migration roadmap, a representative Spring Boot sample, and a cloud recommendation"
    )
    public ResponseEntity<byte[]> download(
            @PathVariable String projectId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        GeneratedReportFile report = generateModernizationReportUseCase.execute(new GetProjectCommand(projectId, principal.getId()));

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(report.suggestedFilename())
                .build();

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(report.content());
    }

}
