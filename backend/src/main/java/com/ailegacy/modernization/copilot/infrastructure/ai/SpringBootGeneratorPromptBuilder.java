package com.ailegacy.modernization.copilot.infrastructure.ai;

import com.ailegacy.modernization.copilot.infrastructure.ai.model.CodeDigest;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Builds the prompt sent to the LLM for representative Spring Boot code
 * generation: converting one sample Servlet to a @RestController and one
 * sample JDBC data access class to Spring Data JPA.
 */
@Component
public class SpringBootGeneratorPromptBuilder {

    public String build(String projectName, CodeDigest digest, List<String> knownTechnologies) {
        String technologiesSection = knownTechnologies == null || knownTechnologies.isEmpty()
                ? ""
                : "\nTechnologies already detected in this project: " + String.join(", ", knownTechnologies) + "\n";

        return """
                You are a Spring Boot Generator inside a legacy application modernization tool. You produce
                representative modern Spring Boot code samples that demonstrate how to modernize specific
                legacy patterns - you do NOT attempt to convert the whole project.

                You will be given a sample of source files extracted from an uploaded legacy project named "%s".
                The sample includes %d of the project's %d files.
                %s
                Your task, using ONLY this sample:
                1. Find ONE representative Servlet class (a class extending HttpServlet, or similarly handling
                   raw HTTP requests) and convert its behavior into a modern Spring Boot @RestController.
                2. Find ONE representative JDBC-based data access class (using java.sql.Connection,
                   PreparedStatement, Statement, or similar) and convert it to Spring Data JPA: an @Entity and a
                   Spring Data JPA Repository interface.
                3. Generate a complete, cohesive, representative set of modern Spring Boot 3 / Java classes
                   covering that one conversion end-to-end: an Entity, a Repository, a DTO, a Service, and a
                   Controller. Use constructor injection, Jakarta annotations, and idiomatic Spring Boot 3 style.
                4. Write a plain-English explanation of what was converted, mapping the original legacy
                   constructs to their modern equivalents and why the change improves the code.

                If the sample contains no Servlet, base the conversion on whatever HTTP-handling code is present.
                If it contains no JDBC code, base the entity/repository on whatever data access pattern is present.
                Always produce a complete, representative example - never leave a field empty.

                Respond with ONLY a single valid JSON object (no markdown code fences, no commentary before or
                after it) with EXACTLY these keys, all string values:
                - "sourceServletReference": which file/class in the sample inspired the controller conversion
                - "sourceJdbcReference": which file/class in the sample inspired the entity/repository conversion
                - "entityCode": the full Java source of the generated @Entity class
                - "repositoryCode": the full Java source of the generated Spring Data JPA repository interface
                - "dtoCode": the full Java source of the generated DTO class
                - "serviceCode": the full Java source of the generated service class
                - "controllerCode": the full Java source of the generated @RestController class
                - "explanation": the plain-English explanation described above

                IMPORTANT: the *Code fields contain full Java source with braces, quotes and newlines. Since the
                overall response must be valid JSON, escape every one of those values properly as JSON strings:
                use \\n for line breaks and \\" for double quotes inside the code. Do not use markdown code
                fences anywhere in your response, including inside the JSON string values.

                Project files:
                %s
                """.formatted(
                projectName, digest.filesIncluded(), digest.totalFiles(), technologiesSection, digest.content()
        );
    }

}
