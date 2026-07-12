package com.ailegacy.modernization.copilot.interfaces.rest.controllers;

import com.ailegacy.modernization.copilot.application.use_cases.planner.GenerateModernizationPlanUseCase;
import com.ailegacy.modernization.copilot.application.use_cases.planner.GetModernizationPlanUseCase;
import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.infrastructure.security.UserPrincipal;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.ApiResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.planner.ModernizationPlanResponse;
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
 * Modernization Planner endpoints.
 *
 * Synthesizes a concrete migration roadmap from an uploaded project, folding
 * in any prior technology/business/architecture/security/performance
 * analyses that already exist for it.
 */
@RestController
@RequestMapping("/projects/{projectId}/modernization-plan")
@RequiredArgsConstructor
@Tag(name = "Modernization Plan", description = "AI-generated migration roadmap synthesized from all available project analysis")
public class ModernizationPlanController {

    private final GenerateModernizationPlanUseCase generateModernizationPlanUseCase;
    private final GetModernizationPlanUseCase getModernizationPlanUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Generate a modernization plan",
            description = "Uses the configured LLM to produce a migration strategy, timeline, complexity, priority matrix, quick wins, risks and required technologies, and stores the result"
    )
    public ApiResponse<ModernizationPlanResponse> run(
            @PathVariable String projectId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ModernizationPlanResponse response = generateModernizationPlanUseCase.execute(new GetProjectCommand(projectId, principal.getId()));
        return ApiResponse.success(response, "Modernization plan generated");
    }

    @GetMapping
    @Operation(summary = "Get modernization plan", description = "Returns the most recently generated modernization plan for this project")
    public ApiResponse<ModernizationPlanResponse> get(
            @PathVariable String projectId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ModernizationPlanResponse response = getModernizationPlanUseCase.execute(new GetProjectCommand(projectId, principal.getId()));
        return ApiResponse.success(response);
    }

}
