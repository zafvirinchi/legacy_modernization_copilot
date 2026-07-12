package com.ailegacy.modernization.copilot.infrastructure.persistence.repositories;

import com.ailegacy.modernization.copilot.domain.entities.GeneratedSpringBootCode;
import com.ailegacy.modernization.copilot.domain.repositories.GeneratedSpringBootCodeRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB implementation of {@link GeneratedSpringBootCodeRepository}.
 */
public interface GeneratedSpringBootCodeMongoRepository extends MongoRepository<GeneratedSpringBootCode, String>, GeneratedSpringBootCodeRepository {
}
