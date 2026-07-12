package com.ailegacy.modernization.copilot.domain.repositories;

import java.util.List;
import java.util.Optional;

/**
 * Base repository interface defining common CRUD operations.
 *
 * All domain repositories should extend this interface to provide
 * standard persistence operations.
 *
 * Implementations:
 * - Spring Data MongoDB repositories, whose generated proxies satisfy these
 *   method signatures automatically (same shape as {@code MongoRepository})
 * - Custom query methods specific to each entity
 */
public interface BaseRepository<T, ID> {

    T save(T entity);

    Optional<T> findById(ID id);

    List<T> findAll();

    void deleteById(ID id);

    boolean existsById(ID id);

}
