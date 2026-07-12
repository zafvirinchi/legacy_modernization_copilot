package com.ailegacy.modernization.copilot.infrastructure.persistence.repositories;

import com.ailegacy.modernization.copilot.domain.entities.User;
import com.ailegacy.modernization.copilot.domain.repositories.UserRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB implementation of {@link UserRepository}.
 */
public interface UserMongoRepository extends MongoRepository<User, String>, UserRepository {
}
