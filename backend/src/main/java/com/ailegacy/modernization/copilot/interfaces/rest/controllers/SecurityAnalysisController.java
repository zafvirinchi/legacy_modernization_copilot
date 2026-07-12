package com.ailegacy.modernization.copilot.interfaces.rest.controllers;

import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.application.use_cases.security.AnalyzeSecurityUseCase;
import com.ailegacy.modernization.copilot.application.use_cases.security.GetSecurityAnalysisUseCase;
import com.ailegacy.modernization.copilot.infrastructure.security.UserPrincipal;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.ApiResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.security.SecurityAnalysisResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Security Analyzer endpoints.
 *
 * Finds concrete security vulnerabilities in an uploaded project and
 * recommends modern Spring Security-based fixes using the configured LLM.
 */
@RestController
@RequestMapping("/projects/{projectId}/security-analysis")
@RequiredArgsConstructor
@Tag(name = "Security Analysis", description = "AI-generated security vulnerability findings and modern Spring Security remediations")
public class SecurityAnalysisController {

    private final AnalyzeSecurityUseCase analyzeSecurityUseCase;
    private final GetSecurityAnalysisUseCase getSecurityAnalysisUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Run security analysis",
            description = "Uses the configured LLM to find security vulnerabilities (SQL injection, hardcoded passwords, weak encryption, missing authentication, session risks, OWASP issues) and stores the result"
    )
    public ApiResponse<SecurityAnalysisResponse> run(
            @PathVariable String projectId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        SecurityAnalysisResponse response = analyzeSecurityUseCase.execute(new GetProjectCommand(projectId, principal.getId()));
        return ApiResponse.success(response, "Security analysis completed");
    }

    @GetMapping
    @Operation(summary = "Get security analysis report", description = "Returns the most recently generated security analysis report for this project")
    public ApiResponse<SecurityAnalysisResponse> get(
            @PathVariable String projectId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        SecurityAnalysisResponse response = getSecurityAnalysisUseCase.execute(new GetProjectCommand(projectId, principal.getId()));
        return ApiResponse.success(response);
    }

}
