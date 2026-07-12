package com.ailegacy.modernization.copilot.infrastructure.persistence.repositories;

import com.ailegacy.modernization.copilot.domain.entities.Project;
import com.ailegacy.modernization.copilot.domain.repositories.ProjectRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB implementation of {@link ProjectRepository}.
 */
public interface ProjectMongoRepository extends MongoRepository<Project, String>, ProjectRepository {
}
