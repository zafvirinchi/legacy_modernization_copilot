package com.ailegacy.modernization.copilot.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

/**
 * Refresh token record persisted in the {@code refresh_tokens} collection.
 *
 * Only a hash of the token's unique identifier (jti) is stored, never the raw token,
 * so a database leak cannot be used to forge or replay sessions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "refresh_tokens")
public class RefreshToken {

    @Id
    private String id;

    private String userId;

    @Indexed(unique = true)
    private String tokenHash;

    private Instant expiresAt;

    @Builder.Default
    private boolean revoked = false;

    @CreatedDate
    private Instant createdAt;

}
