package com.ailegacy.modernization.copilot.infrastructure.ai;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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

    @Value("${llm.openai.model:gpt-4}")
    private String model;

    @Value("${llm.openai.temperature:0.7}")
    private double temperature;

    @Value("${llm.openai.max-tokens:4096}")
    private int maxTokens;

    /**
     * OpenAI Chat Language Model Bean
     */
    @Bean
    public ChatLanguageModel chatLanguageModel() {
        if (openAiApiKey == null || openAiApiKey.isBlank()) {
            log.warn("OpenAI API key not configured. AI features will be disabled.");
            return null;
        }

        log.info("Initializing OpenAI Chat Language Model | model={} | temperature={} | maxTokens={}", 
                model, temperature, maxTokens);

        return OpenAiChatModel.builder()
                .apiKey(openAiApiKey)
                .modelName(model)
                .temperature(temperature)
                .maxTokens(maxTokens)
                .build();
    }

    /**
     * OpenAI Embedding Model Bean
     */
    @Bean
    public EmbeddingModel embeddingModel() {
        if (openAiApiKey == null || openAiApiKey.isBlank()) {
            log.warn("OpenAI API key not configured. Embedding features will be disabled.");
            return null;
        }

        log.info("Initializing OpenAI Embedding Model");

        return OpenAiEmbeddingModel.builder()
                .apiKey(openAiApiKey)
                .modelName("text-embedding-3-small")
                .build();
    }

}
