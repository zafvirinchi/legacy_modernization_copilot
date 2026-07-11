package com.ailegacy.modernization.copilot.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB configuration.
 * 
 * Enables:
 * - MongoDB repositories
 * - Auditing for created/modified timestamps
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.ailegacy.modernization.copilot.infrastructure.persistence")
@EnableMongoAuditing
public class MongoConfig {

}
