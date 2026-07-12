package com.ailegacy.modernization.copilot.domain.repositories;

import com.ailegacy.modernization.copilot.domain.entities.GeneratedSpringBootCode;

import java.util.Optional;

/**
 * Repository contract for {@link GeneratedSpringBootCode} persistence.
 */
public interface GeneratedSpringBootCodeRepository extends BaseRepository<GeneratedSpringBootCode, String> {

    Optional<GeneratedSpringBootCode> findByProjectId(String projectId);

    void deleteByProjectId(String projectId);

}
