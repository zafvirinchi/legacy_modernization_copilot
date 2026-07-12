package com.ailegacy.modernization.copilot.domain.repositories;

import com.ailegacy.modernization.copilot.domain.entities.RefreshToken;

import java.util.Optional;

/**
 * Repository contract for {@link RefreshToken} persistence.
 */
public interface RefreshTokenRepository extends BaseRepository<RefreshToken, String> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void deleteByUserId(String userId);

}
