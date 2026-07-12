package com.ailegacy.modernization.copilot.interfaces.rest.controllers;

import com.ailegacy.modernization.copilot.application.use_cases.analysis.AnalyzeBusinessLogicUseCase;
import com.ailegacy.modernization.copilot.application.use_cases.analysis.GetBusinessAnalysisUseCase;
import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.infrastructure.security.UserPrincipal;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.ApiResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.analysis.BusinessAnalysisResponse;
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
 * Business Logic Analyzer endpoints.
 *
 * Explains an uploaded project's business purpose, modules, workflows and
 * entities in plain English using the configured LLM.
 */
@RestController
@RequestMapping("/projects/{projectId}/business-analysis")
@RequiredArgsConstructor
@Tag(name = "Business Analysis", description = "AI-generated plain-English explanation of an uploaded project's business logic")
public class BusinessAnalysisController {

    private final AnalyzeBusinessLogicUseCase analyzeBusinessLogicUseCase;
    private final GetBusinessAnalysisUseCase getBusinessAnalysisUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Run business logic analysis",
            description = "Uses the configured LLM to explain the project's business purpose, modules, workflows and entities, and stores the result"
    )
    public ApiResponse<BusinessAnalysisResponse> run(
            @PathVariable String projectId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        BusinessAnalysisResponse response = analyzeBusinessLogicUseCase.execute(new GetProjectCommand(projectId, principal.getId()));
        return ApiResponse.success(response, "Business analysis completed");
    }

    @GetMapping
    @Operation(summary = "Get business analysis report", description = "Returns the most recently generated business analysis report for this project")
    public ApiResponse<BusinessAnalysisResponse> get(
            @PathVariable String projectId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        BusinessAnalysisResponse response = getBusinessAnalysisUseCase.execute(new GetProjectCommand(projectId, principal.getId()));
        return ApiResponse.success(response);
    }

}
