package com.ailegacy.modernization.copilot.infrastructure.ai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

/**
 * LangChain4j configuration for AI model integration.
 * 
 * Supports:
 * - OpenAI GPT-4 and GPT-3.5
 * - Azure OpenAI
 * - Embeddings for semantic search
 * - Custom model parameters
 */
@Slf4j
@Configuration
public class LangChain4jConfig {

    @Value("${llm.openai.api-key:}")
    private String openAiApiKey;

    @Value("${llm.openai.base-url:}")
    private String baseUrl;

    @Value("${llm.openai.model:gpt-4}")
    private String model;

    @Value("${llm.openai.temperature:0.7}")
    private double temperature;

    @Value("${llm.openai.max-tokens:4096}")
    private int maxTokens;

    /**
     * OpenAI Chat Language Model Bean.
     *
     * {@code @Lazy}: this bean must never be constructed during application
     * startup. Building the underlying HTTP client is the one thing in this
     * codebase that talks to an external host during bean creation, and if
     * that host is unreachable (or the constructor otherwise throws), an
     * eager bean would fail {@code ApplicationContext.refresh()} and take
     * the whole application down - including the embedded web server -
     * before Tomcat ever binds to a port. Consumers must inject this via
     * {@code ObjectProvider<ChatLanguageModel>} and resolve it lazily at the
     * point of use (see the analyzer classes in this package), not as a
     * direct constructor dependency, so a null/absent/failed model never
     * blocks startup and is only ever discovered when an AI feature is
     * actually invoked.
     */
    @Lazy
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        if (openAiApiKey == null || openAiApiKey.isBlank()) {
            log.warn("OpenAI API key not configured. AI features will be disabled.");
            return null;
        }

        log.info("Initializing OpenAI Chat Language Model | model={} | temperature={} | maxTokens={} | baseUrl={}",
                model, temperature, maxTokens, baseUrl.isBlank() ? "default" : baseUrl);

        try {
            var builder = OpenAiChatModel.builder()
                    .apiKey(openAiApiKey)
                    .modelName(model)
                    .temperature(temperature)
                    .maxTokens(maxTokens);

            if (!baseUrl.isBlank()) {
                builder.baseUrl(baseUrl);
            }

            return builder.build();
        } catch (Exception ex) {
            log.warn("Failed to initialize OpenAI Chat Language Model - AI features will be disabled. Cause: {}",
                    ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * OpenAI Embedding Model Bean. {@code @Lazy} for the same reason as
     * {@link #chatLanguageModel()} above.
     */
    @Lazy
    @Bean
    public EmbeddingModel embeddingModel() {
        if (openAiApiKey == null || openAiApiKey.isBlank()) {
            log.warn("OpenAI API key not configured. Embedding features will be disabled.");
            return null;
        }

        log.info("Initializing OpenAI Embedding Model");

        try {
            var builder = OpenAiEmbeddingModel.builder()
                    .apiKey(openAiApiKey)
                    .modelName("text-embedding-3-small");

            if (!baseUrl.isBlank()) {
                builder.baseUrl(baseUrl);
            }

            return builder.build();
        } catch (Exception ex) {
            log.warn("Failed to initialize OpenAI Embedding Model - embedding features will be disabled. Cause: {}",
                    ex.getMessage(), ex);
            return null;
        }
    }

}
