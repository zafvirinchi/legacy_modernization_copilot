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
 * AI-generated representative Spring Boot code for an uploaded project,
 * converting one sample Servlet to a @RestController and one sample JDBC
 * data access class to Spring Data JPA, persisted in the
 * {@code generated_spring_boot_code} collection.
 *
 * This is deliberately a single representative example, not a full project
 * conversion.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "generated_spring_boot_code")
public class GeneratedSpringBootCode {

    @Id
    private String id;

    @Indexed(unique = true)
    private String projectId;

    private String sourceServletReference;

    private String sourceJdbcReference;

    private String entityCode;

    private String repositoryCode;

    private String dtoCode;

    private String serviceCode;

    private String controllerCode;

    private String explanation;

    private int filesAnalyzed;

    private int totalProjectFiles;

    @CreatedDate
    private Instant createdAt;

}
