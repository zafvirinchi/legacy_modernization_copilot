package com.ailegacy.modernization.copilot.application.mappers;

/**
 * Interface for DTO mappers using MapStruct or manual mapping.
 * 
 * Implementations should handle:
 * - Entity to DTO conversion
 * - DTO to Entity conversion
 * - Nested object mapping
 * 
 * Usage:
 * @Mapper(componentModel = "spring")
 * public interface ProjectMapper {
 *     ProjectDto toDto(Project entity);
 *     Project toEntity(ProjectDto dto);
 * }
 */
public interface BaseMapper<E, D> {

    D toDto(E entity);

    E toEntity(D dto);

}
