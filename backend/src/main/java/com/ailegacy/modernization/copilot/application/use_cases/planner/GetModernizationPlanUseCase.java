package com.ailegacy.modernization.copilot.application.use_cases.planner;

import com.ailegacy.modernization.copilot.application.mappers.ModernizationPlanMapper;
import com.ailegacy.modernization.copilot.application.use_cases.UseCase;
import com.ailegacy.modernization.copilot.application.use_cases.project.GetProjectCommand;
import com.ailegacy.modernization.copilot.domain.entities.ModernizationPlan;
import com.ailegacy.modernization.copilot.domain.exceptions.ResourceNotFoundException;
import com.ailegacy.modernization.copilot.domain.repositories.ModernizationPlanRepository;
import com.ailegacy.modernization.copilot.domain.repositories.ProjectRepository;
import com.ailegacy.modernization.copilot.interfaces.rest.dto.planner.ModernizationPlanResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Retrieves a previously generated modernization plan, scoped to its
 * project's owner.
 */
@Component
@RequiredArgsConstructor
public class GetModernizationPlanUseCase implements UseCase<GetProjectCommand, ModernizationPlanResponse> {

    private final ProjectRepository projectRepository;
    private final ModernizationPlanRepository modernizationPlanRepository;
    private final ModernizationPlanMapper modernizationPlanMapper;

    @Override
    public ModernizationPlanResponse execute(GetProjectCommand command) {
        projectRepository.findByIdAndOwnerId(command.projectId(), command.ownerId())
                .orElseThrow(() -> new ResourceNotFoundException("Project", command.projectId()));

        ModernizationPlan plan = modernizationPlanRepository.findByProjectId(command.projectId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No modernization plan found for this project. Generate one first."));

        return modernizationPlanMapper.toResponse(plan);
    }

}
