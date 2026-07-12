package com.ailegacy.modernization.copilot.domain.repositories;

import com.ailegacy.modernization.copilot.domain.entities.User;

import java.util.Optional;

/**
 * Repository contract for {@link User} persistence.
 */
public interface UserRepository extends BaseRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

}
