package com.ailegacy.modernization.copilot.interfaces.rest.controllers;

import com.ailegacy.modernization.copilot.application.use_cases.performance.AnalyzePerformanceUseCase;
import com.ailegacy.modernization.copilot.application.use_cases.performance.GetPerformanceAnalysisUseCase;
import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.infrastructure.security.UserPrincipal;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.ApiResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.performance.PerformanceAnalysisResponse;
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
 * Performance Analyzer endpoints.
 *
 * Finds concrete performance and code-quality issues in an uploaded project
 * and recommends modern alternatives using the configured LLM.
 */
@RestController
@RequestMapping("/projects/{projectId}/performance-analysis")
@RequiredArgsConstructor
@Tag(name = "Performance Analysis", description = "AI-generated performance/code-quality findings and modern alternatives")
public class PerformanceAnalysisController {

    private final AnalyzePerformanceUseCase analyzePerformanceUseCase;
    private final GetPerformanceAnalysisUseCase getPerformanceAnalysisUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Run performance analysis",
            description = "Uses the configured LLM to find performance issues (N+1 queries, large classes, god objects, memory leak risks, duplicate code, blocking IO) and stores the result"
    )
    public ApiResponse<PerformanceAnalysisResponse> run(
            @PathVariable String projectId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        PerformanceAnalysisResponse response = analyzePerformanceUseCase.execute(new GetProjectCommand(projectId, principal.getId()));
        return ApiResponse.success(response, "Performance analysis completed");
    }

    @GetMapping
    @Operation(summary = "Get performance analysis report", description = "Returns the most recently generated performance analysis report for this project")
    public ApiResponse<PerformanceAnalysisResponse> get(
            @PathVariable String projectId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        PerformanceAnalysisResponse response = getPerformanceAnalysisUseCase.execute(new GetProjectCommand(projectId, principal.getId()));
        return ApiResponse.success(response);
    }

}
