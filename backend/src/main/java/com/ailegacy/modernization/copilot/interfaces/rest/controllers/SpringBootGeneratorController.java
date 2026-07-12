package com.ailegacy.modernization.copilot.interfaces.rest.controllers;

import com.ailegacy.modernization.copilot.application.use_cases.generator.GenerateSpringBootCodeUseCase;
import com.ailegacy.modernization.copilot.application.use_cases.generator.GetGeneratedSpringBootCodeUseCase;
import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.infrastructure.security.UserPrincipal;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.ApiResponse;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.generator.GeneratedSpringBootCodeResponse;
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
 * Spring Boot Generator endpoints.
 *
 * Produces one representative modern Spring Boot example (Entity, Repository,
 * DTO, Service, Controller) converting a sample Servlet to a @RestController
 * and a sample JDBC class to Spring Data JPA. This never attempts a full
 * project conversion.
 */
@RestController
@RequestMapping("/projects/{projectId}/spring-boot-generation")
@RequiredArgsConstructor
@Tag(name = "Spring Boot Generator", description = "AI-generated representative Spring Boot code (Servlet to @RestController, JDBC to Spring Data JPA)")
public class SpringBootGeneratorController {

    private final GenerateSpringBootCodeUseCase generateSpringBootCodeUseCase;
    private final GetGeneratedSpringBootCodeUseCase getGeneratedSpringBootCodeUseCase;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Generate representative Spring Boot code",
            description = "Uses the configured LLM to convert one sample Servlet to a @RestController and one sample JDBC class to Spring Data JPA, generating an Entity, Repository, DTO, Service and Controller plus an explanation. Does not convert the whole project."
    )
    public ApiResponse<GeneratedSpringBootCodeResponse> run(
            @PathVariable String projectId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        GeneratedSpringBootCodeResponse response = generateSpringBootCodeUseCase.execute(new GetProjectCommand(projectId, principal.getId()));
        return ApiResponse.success(response, "Representative Spring Boot code generated");
    }

    @GetMapping
    @Operation(summary = "Get generated Spring Boot code", description = "Returns the most recently generated representative Spring Boot example for this project")
    public ApiResponse<GeneratedSpringBootCodeResponse> get(
            @PathVariable String projectId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        GeneratedSpringBootCodeResponse response = getGeneratedSpringBootCodeUseCase.execute(new GetProjectCommand(projectId, principal.getId()));
        return ApiResponse.success(response);
    }

}
