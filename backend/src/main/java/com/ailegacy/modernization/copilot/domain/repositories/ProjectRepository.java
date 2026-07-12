package com.ailegacy.modernization.copilot.domain.repositories;

import com.ailegacy.modernization.copilot.domain.entities.Project;

import java.util.List;
import java.util.Optional;

/**
 * Repository contract for {@link Project} persistence.
 */
public interface ProjectRepository extends BaseRepository<Project, String> {

    List<Project> findByOwnerIdOrderByCreatedAtDesc(String ownerId);

    Optional<Project> findByIdAndOwnerId(String id, String ownerId);

}
