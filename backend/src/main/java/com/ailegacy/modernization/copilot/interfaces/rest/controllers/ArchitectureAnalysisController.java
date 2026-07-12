package com.ailegacy.modernization.copilot.interfaces.rest.controllers;

import com.ailegacy.modernization.copilot.application.use_cases.architecture.AnalyzeArchitectureUseCase;
import com.ailegacy.modernization.copilot.application.use_cases.architecture.GetArchitectureAnalysisUseCase;
import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.infrastructure.security.UserPrincipal;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.ApiResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.architecture.ArchitectureAnalysisResponse;
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
 * Architecture Analyzer endpoints.
 *
 * Classifies an uploaded project's architecture, scores it, and generates
 * Mermaid diagrams for the current and target architectures using the
 * configured LLM.
 */
@RestController
@RequestMapping("/projects/{projectId}/architecture-analysis")
@RequiredArgsConstructor
@Tag(name = "Architecture Analysis", description = "AI-generated architecture classification, scoring and migration diagrams")
public class ArchitectureAnalysisController {

    private final AnalyzeArchitectureUseCase analyzeArchitectureUseCase;
    private final GetArchitectureAnalysisUseCase getArchitectureAnalysisUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Run architecture analysis",
            description = "Uses the configured LLM to classify the architecture, score it, and generate current/target Mermaid diagrams, and stores the result"
    )
    public ApiResponse<ArchitectureAnalysisResponse> run(
            @PathVariable String projectId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ArchitectureAnalysisResponse response = analyzeArchitectureUseCase.execute(new GetProjectCommand(projectId, principal.getId()));
        return ApiResponse.success(response, "Architecture analysis completed");
    }

    @GetMapping
    @Operation(summary = "Get architecture analysis report", description = "Returns the most recently generated architecture analysis report for this project")
    public ApiResponse<ArchitectureAnalysisResponse> get(
            @PathVariable String projectId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ArchitectureAnalysisResponse response = getArchitectureAnalysisUseCase.execute(new GetProjectCommand(projectId, principal.getId()));
        return ApiResponse.success(response);
    }

}
