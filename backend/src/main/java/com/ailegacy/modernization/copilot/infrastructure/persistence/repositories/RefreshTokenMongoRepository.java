package com.ailegacy.modernization.copilot.infrastructure.persistence.repositories;

import com.ailegacy.modernization.copilot.domain.entities.RefreshToken;
import com.ailegacy.modernization.copilot.domain.repositories.RefreshTokenRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB implementation of {@link RefreshTokenRepository}.
 */
public interface RefreshTokenMongoRepository extends MongoRepository<RefreshToken, String>, RefreshTokenRepository {
}
