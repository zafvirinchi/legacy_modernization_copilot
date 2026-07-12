package com.ailegacy.modernization.copilot.infrastructure.persistence.repositories;

import com.ailegacy.modernization.copilot.domain.entities.ModernizationPlan;
import com.ailegacy.modernization.copilot.domain.repositories.ModernizationPlanRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB implementation of {@link ModernizationPlanRepository}.
 */
public interface ModernizationPlanMongoRepository extends MongoRepository<ModernizationPlan, String>, ModernizationPlanRepository {
}
