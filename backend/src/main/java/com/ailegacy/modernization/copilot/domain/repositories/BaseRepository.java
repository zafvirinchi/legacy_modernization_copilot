package com.ailegacy.modernization.copilot.domain.repositories;

/**
 * Base repository interface defining common CRUD operations.
 * 
 * All domain repositories should extend this interface to provide
 * standard persistence operations.
 * 
 * Implementations:
 * - Spring Data MongoDB repositories
 * - Custom query methods specific to each entity
 */
public interface BaseRepository<T, ID> {

    // Custom repository methods will be defined in concrete implementations
    // Spring Data provides standard CRUD operations automatically

}
