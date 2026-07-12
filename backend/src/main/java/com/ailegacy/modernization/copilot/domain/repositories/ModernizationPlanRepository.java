package com.ailegacy.modernization.copilot.domain.repositories;

import com.ailegacy.modernization.copilot.domain.entities.ModernizationPlan;

import java.util.Optional;

/**
 * Repository contract for {@link ModernizationPlan} persistence.
 */
public interface ModernizationPlanRepository extends BaseRepository<ModernizationPlan, String> {

    Optional<ModernizationPlan> findByProjectId(String projectId);

    void deleteByProjectId(String projectId);

}
