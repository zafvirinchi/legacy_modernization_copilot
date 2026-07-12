package com.ailegacy.modernization.copilot.infrastructure.persistence.repositories;

import com.ailegacy.modernization.copilot.domain.entities.TechnologyDetectionResult;
import com.ailegacy.modernization.copilot.domain.repositories.TechnologyDetectionRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB implementation of {@link TechnologyDetectionRepository}.
 */
public interface TechnologyDetectionMongoRepository extends MongoRepository<TechnologyDetectionResult, String>, TechnologyDetectionRepository {
}
